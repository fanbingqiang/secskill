package com.bq.dto;

import lombok.Data;

@Data
public class OrderCreateDTO {
    private Long goodsId;
    private Integer quantity;
}