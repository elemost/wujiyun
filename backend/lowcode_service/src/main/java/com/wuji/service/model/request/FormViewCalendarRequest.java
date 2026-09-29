package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FormViewCalendarRequest {
    private Date startTime;

    private Date endTime;

    private String formId;

    private String applicationId;

    private MongodbSearchFilter filter;

    private List<String> keyList;

    private String keyword;

    private String groupId;

    // 日：DAY 小时：HOUR
    private String lat;
}
