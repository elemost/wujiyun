package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormDataTitle;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamTriggerLogVO {
    private String uuid;

    private String dataStreamId;

    private Integer dataStreamVersion;

    private String creator;

    private String createName;

    private String action;

    private String applicationId;

    private String formId;

    private String formName;

    private FormDataTitle title;

    private Long triggerStartTime;

    private Long triggerEndTime;

    private String result;

    private String exceptionError;

    private List<String> parentList;

    private String lastParentId;
}
