package com.bq.controller;

import com.bq.common.PageResult;
import com.bq.common.Result;
import com.bq.dto.OrderCreateDTO;
import com.bq.service.OrderService;
import com.bq.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // POST /api/order/create
    @PostMapping("/create")
    public Result<OrderVO> create(@RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.create(dto));
    }

    // GET /api/order/{orderNo}
    @GetMapping("/{orderNo}")
    public Result<OrderVO> detail(@PathVariable String orderNo) {
        return Result.success(orderService.getDetail(orderNo));
    }

    @PostMapping("/{orderNo}/pay")
    public Result<Void> pay(@PathVariable String orderNo) {
        orderService.pay(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo) {
        orderService.cancel(orderNo);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<PageResult<OrderVO>> myOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.myOrders(status, page, size));
    }
}