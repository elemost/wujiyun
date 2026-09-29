package com.wuji.factory.schedule;

import com.wuji.factory.service.FormDataFactoryExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("formDataFactorySchedule")
@Slf4j
public class FormDataFactorySchedule {

    @Autowired
    private FormDataFactoryExecuteService formDataFactoryExecuteService;

    @Value("${lowcode.runjob:0}")
    private Integer activeProfile;

    public void trigger(String id, String applicationId) {
        log.info("数据工厂 同步表单 id = {} application = {}", id, applicationId);
        if (0 == activeProfile) {
            return;
        }
        formDataFactoryExecuteService.syncDataExecute(id, applicationId);
    }
}
