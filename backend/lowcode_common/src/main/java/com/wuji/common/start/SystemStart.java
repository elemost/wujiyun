package com.wuji.common.start;



import com.wuji.common.start.handler.StartHandler;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Component
public class SystemStart implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        List<StartHandler> values = new ArrayList<>(ToolSpring.getBeansOfType(StartHandler.class).values());
        Collections.sort(values, new Comparator<StartHandler>() {
            @Override
            public int compare(StartHandler o1, StartHandler o2) {
                return o2.getWeight() - o1.getWeight();
            }
        });
        log.info("各模块开始执行初始化动作");
        for (StartHandler value : values) {
            value.initAction();
        }
        log.info("各模块执行初始化动作结束");
    }
}
