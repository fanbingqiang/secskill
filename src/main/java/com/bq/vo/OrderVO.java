package com.bq.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private String orderNo;
    private Long goodsId;
    private String goodsName;
    private BigDecimal amount;
    private Integer status;
    private String statusDesc;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private LocalDateTime payTime;
}