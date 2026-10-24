package com.bq.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bq.common.OrderStatusEnum;
import com.bq.common.PageResult;
import com.bq.dto.OrderCreateDTO;
import com.bq.entity.Goods;
import com.bq.entity.Order;
import com.bq.exception.BizException;
import com.bq.mapper.GoodsMapper;
import com.bq.mapper.OrderMapper;
import com.bq.service.OrderService;
import com.bq.util.DelayQueueUtil;
import com.bq.util.OrderNoUtil;
import com.bq.util.UserContext;
import com.bq.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderNoUtil orderNoUtil;

    @Autowired
    private DelayQueueUtil delayQueueUtil;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException(401, "未登录");
        }

        // 1. 查商品
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new BizException(50002, "商品不存在");
        }
        if (goods.getStatus() != 1) {
            throw new BizException(50002, "商品已下架");
        }

        int qty = dto.getQuantity() == null ? 1 : dto.getQuantity();
        int affected = goodsMapper.deductStock(dto.getGoodsId(), qty);
        if (affected == 0) {
            throw new BizException(50001, "库存不足");
        }

        // 3. 生成订单号
        String orderNo = orderNoUtil.generate();

        // 4. 写订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setActivityId(null);              // 普通订单
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getName());    // ★ 快照
        order.setAmount(goods.getPrice().multiply(new java.math.BigDecimal(qty)));
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));   // 30 分钟支付
        orderMapper.insert(order);


        orderMapper.insert(order);

        delayQueueUtil.offer(orderNo, 30, TimeUnit.MINUTES);

        // 5. 返回 VO
        return toVO(order);
    }

    @Override
    public OrderVO getDetail(String orderNo) {
        Order order = orderMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BizException(50005, "订单不存在");
        }
        return toVO(order);
    }

    private OrderVO toVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setGoodsId(order.getGoodsId());
        vo.setGoodsName(order.getGoodsName());
        vo.setAmount(order.getAmount());
        vo.setStatus(order.getStatus());
        OrderStatusEnum e = OrderStatusEnum.of(order.getStatus());
        vo.setStatusDesc(e == null ? "未知" : e.getDesc());
        vo.setCreateTime(order.getCreateTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setPayTime(order.getPayTime());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(String orderNo) {
        // 1. 查订单
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BizException(50005, "订单不存在");
        }

        // 2. 校验状态：只有待支付能支付
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BizException(50006, "订单状态异常，无法支付");
        }

        // 3. 校验是否超时
        if (order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BizException(50007, "订单已超时");
        }

        // 4. 改状态
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BizException(50005, "订单不存在");
        }

        // 只有待支付能取消
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BizException(50006, "订单状态异常，无法取消");
        }

        // 改状态为已取消
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);


        goodsMapper.rollbackStock(order.getGoodsId(), 1);
    }

    @Override
    public PageResult<OrderVO> myOrders(Integer status, Integer page, Integer size) {
        Long userId = UserContext.getUserId();

        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)  // status 为空就不加条件
                .orderByDesc(Order::getCreateTime);

        Page<Order> result = orderMapper.selectPage(p, wrapper);

        List<OrderVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrder(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            log.warn("订单不存在: {}", orderNo);
            return;
        }

        // 只有待支付才取消（已支付/已取消直接跳过）
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            log.info("订单状态非待支付，跳过: orderNo={}, status={}", orderNo, order.getStatus());
            return;
        }

        // 改状态
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 回滚库存
        goodsMapper.rollbackStock(order.getGoodsId(), 1);

        log.info("超时订单已取消: orderNo={}", orderNo);
    }
}