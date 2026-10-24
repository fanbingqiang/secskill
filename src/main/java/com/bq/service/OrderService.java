package com.bq.service;

import com.bq.common.PageResult;
import com.bq.dto.OrderCreateDTO;
import com.bq.vo.OrderVO;

public interface OrderService {
    OrderVO create(OrderCreateDTO dto);
    OrderVO getDetail(String orderNo);
    void pay(String orderNo);
    void cancel(String orderNo);
    PageResult<OrderVO> myOrders(Integer status, Integer page, Integer size);

    void cancelTimeoutOrder(String orderNo);
}