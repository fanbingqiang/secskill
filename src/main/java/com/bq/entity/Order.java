package com.bq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long activityId;      // 秒杀订单才有，普通订单为 null
    private Long goodsId;
    private String goodsName;     // 快照
    private BigDecimal amount;
    private Integer status;       // 0待支付 1已支付 2已取消 3已完成
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
}