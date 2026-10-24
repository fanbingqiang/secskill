package com.bq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.bq.common.Result;

/**
 * 秒杀系统启动类
 *
 * @SpringBootApplication：开启组件扫描 + 自动配置，扫描范围为 com.bq 及其子包
 * @MapperScan：指定 Mapper 接口所在包，避免每个接口上重复写 @Mapper
 */
@SpringBootApplication
@MapperScan("com.bq.mapper")
public class SecskillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecskillApplication.class, args);
    }
}