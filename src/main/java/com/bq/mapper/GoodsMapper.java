package com.bq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bq.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    @Update("UPDATE t_goods SET stock = stock - #{qty} WHERE id = #{id} AND stock >= #{qty}")
    int deductStock(@Param("id") Long id, @Param("qty") Integer qty);

    // 回滚库存
    @Update("UPDATE t_goods SET stock = stock + #{qty} WHERE id = #{id}")
    int rollbackStock(@Param("id") Long id, @Param("qty") Integer qty);
}