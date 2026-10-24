package com.bq.task;

import com.bq.service.OrderService;
import com.bq.util.DelayQueueUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTimeoutConsumer {

    @Autowired
    private DelayQueueUtil delayQueueUtil;

    @Autowired
    private OrderService orderService;

    // 项目启动时开一个线程，一直阻塞等待延迟消息
    @PostConstruct
    public void start() {
        new Thread(() -> {
            RBlockingQueue<String> queue = delayQueueUtil.getBlockingQueue();
            while (true) {
                try {
                    // take() 阻塞，直到有消息
                    String orderNo = queue.take();
                    log.info("收到延迟消息，处理超时订单: {}", orderNo);
                    orderService.cancelTimeoutOrder(orderNo);
                } catch (Exception e) {
                    log.error("处理延迟消息失败", e);
                }
            }
        }, "order-timeout-consumer").start();
    }
}