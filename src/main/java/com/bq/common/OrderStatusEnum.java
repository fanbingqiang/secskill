package com.bq.common;

import lombok.Getter;
import java.util.Set;

@Getter
public enum OrderStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    CANCELED(2, "已取消"),
    FINISHED(3, "已完成");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum of(Integer code) {
        for (OrderStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    // ★ 定义每个状态能流转到哪些状态
    public Set<OrderStatusEnum> nextStates() {
        switch (this) {
            case PENDING:
                return Set.of(PAID, CANCELED);   // 待支付 → 已支付 / 已取消
            case PAID:
                return Set.of(FINISHED);         // 已支付 → 已完成
            case CANCELED:
            case FINISHED:
                return Set.of();                 // 终态，不能流转
            default:
                return Set.of();
        }
    }

    // ★ 判断能否从 from 流转到 to
    public static boolean canTransfer(Integer fromCode, OrderStatusEnum to) {
        OrderStatusEnum from = of(fromCode);
        if (from == null) {return false;}
        return from.nextStates().contains(to);
    }
}