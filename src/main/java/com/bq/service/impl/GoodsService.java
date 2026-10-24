package com.bq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bq.common.PageResult;
import com.bq.entity.Goods;
import com.bq.exception.BizException;
import com.bq.mapper.GoodsMapper;
import com.bq.service.GoodsServiceImpl;
import com.bq.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService implements GoodsServiceImpl {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageResult<GoodsVO> list(Integer page, Integer size) {
        Page<Goods> p = new Page<>(page, size);
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, 1)
                .orderByDesc(Goods::getCreateTime);

        Page<Goods> result = goodsMapper.selectPage(p, wrapper);


        List<GoodsVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    public GoodsVO detail(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            throw new BizException(50002, "商品不存在");
        }
        return toVO(goods);
    }

    // Entity → VO 转换
    private GoodsVO toVO(Goods goods) {
        GoodsVO vo = new GoodsVO();
        vo.setId(goods.getId());
        vo.setName(goods.getName());
        vo.setPrice(goods.getPrice());
        vo.setStock(goods.getStock());
        vo.setImg(goods.getImg());
        return vo;
    }
}