package com.wuji.admin.start;


import com.wuji.common.service.ConfigService;
import com.wuji.common.start.handler.StartHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ConfigInitHandlerImpl")
@Slf4j
public class ConfigInitHandlerImpl implements StartHandler {
    @Autowired
    private ConfigService configService;

    @Override
    public void initAction() {
        try {
            log.info("配置初始化开始");
            configService.init();
            log.info("配置初始化结束");
        } catch (Exception e) {
            log.error("初始化配置失败", e);
        }
    }
}
