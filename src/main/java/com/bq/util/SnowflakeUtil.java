package com.bq.util;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeUtil {

    // 起始时间：2024-01-01
    private static final long EPOCH = 1704067200000L;

    // 机器 ID 占 10 位
    private static final long WORKER_ID_BITS = 10L;
    // 序列号占 12 位
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeUtil() {
        // 生产环境从配置或机器 IP 读
        this.workerId = 1L;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨检测
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成 ID");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 当前毫秒序列号用完，等下一毫秒
                while ((timestamp = System.currentTimeMillis()) == lastTimestamp) {
                    // 自旋
                }
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }
}