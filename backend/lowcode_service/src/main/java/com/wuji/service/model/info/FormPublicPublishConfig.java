package com.wuji.service.model.info;

import lombok.Data;

import java.util.Date;

@Data
public class FormPublicPublishConfig {
    private Date startTime;

    private Date endTime;

    // custom perpetuity
    private String inDateLimit;

}
