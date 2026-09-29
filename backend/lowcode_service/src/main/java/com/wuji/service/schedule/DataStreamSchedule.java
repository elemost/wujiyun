package com.wuji.service.schedule;

import com.wuji.service.service.FormDataStreamPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("dataStreamSchedule")
@Slf4j
public class DataStreamSchedule {

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Value("${lowcode.runjob:0}")
    private Integer activeProfile;

    public void trigger(String dataStreamId, String applicationId) {
        log.info("数智助手 同步表单 id = {} application = {}", dataStreamId, applicationId);
        if (0 == activeProfile) {
            return;
        }
        formDataStreamPublishService.timeTrigger(dataStreamId, applicationId);
    }
}
