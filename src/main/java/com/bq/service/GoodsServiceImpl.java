package com.bq.service;

import com.bq.common.PageResult;
import com.bq.vo.GoodsVO;

public interface GoodsServiceImpl {

    PageResult<GoodsVO> list(Integer page, Integer size) ;

    GoodsVO detail(Long id);
}