package com.bq.controller;

import com.bq.common.PageResult;
import com.bq.common.Result;
import com.bq.service.impl.GoodsService;
import com.bq.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    // GET /api/goods/list?page=1&size=10
    @GetMapping("/list")
    public Result<PageResult<GoodsVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(goodsService.list(page, size));
    }

    // GET /api/goods/{id}
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id) {
        return Result.success(goodsService.detail(id));
    }
}