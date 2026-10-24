package com.bq.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OrderNoUtil {

    // 简化版：时间戳 + 随机数，保证基本唯一
    // 真实项目用雪花算法（Snowflake）或 Redis 自增
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generate() {
        String time = LocalDateTime.now().format(FMT);
        // 6 位随机数
        int random = (int) (Math.random() * 1000000);
        return time + String.format("%06d", random);
    }
}