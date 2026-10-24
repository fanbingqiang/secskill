package com.bq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_goods")
public class Goods {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String img;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}