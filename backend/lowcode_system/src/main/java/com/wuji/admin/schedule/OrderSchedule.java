package com.wuji.admin.schedule;

import com.wuji.admin.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component("orderSchedule")
public class OrderSchedule {

    @Autowired
    private OrdersService ordersService;

    @Scheduled(initialDelay = 0, fixedDelay = 10 * 60 * 1000)
    public void sendPayMessage() {
        log.info("发送企业微信支付短信");
        ordersService.sendPayMessage();
        log.info("结束定时任务");
    }
}
