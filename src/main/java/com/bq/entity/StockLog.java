package com.bq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_stock_log")
public class StockLog {
    private Long id;
    private Long activityId;
    private String orderNo;
    private Integer changeType;   // 1扣减 2回滚
    private LocalDateTime createTime;
}