package com.bq.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GoodsVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String img;
}