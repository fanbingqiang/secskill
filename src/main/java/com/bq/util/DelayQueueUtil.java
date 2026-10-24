package com.bq.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DelayQueueUtil {

    @Autowired
    private RedissonClient redissonClient;

    private RBlockingQueue<String> blockingQueue;
    private RDelayedQueue<String> delayedQueue;

    // 队列名
    private static final String QUEUE_NAME = "order:delay:queue";

    @PostConstruct
    public void init() {
        // 阻塞队列：真正消费的队列
        blockingQueue = redissonClient.getBlockingQueue(QUEUE_NAME);
        // 延迟队列：往这里塞，到期后自动转到 blockingQueue
        delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
    }

    // 投递延迟消息
    public void offer(String orderNo, long delay, TimeUnit unit) {
        delayedQueue.offer(orderNo, delay, unit);
        log.info("延迟消息已投递: orderNo={}, delay={}{}", orderNo, delay, unit);
    }

    // 获取阻塞队列（消费者用）
    public RBlockingQueue<String> getBlockingQueue() {
        return blockingQueue;
    }
}