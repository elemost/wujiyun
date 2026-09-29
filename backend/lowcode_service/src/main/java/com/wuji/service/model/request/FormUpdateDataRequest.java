package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormUpdateDataRequest {
    private String uuid;

    private String applicationId;

    private String formId;

    private Integer version;

    private JSONObject instValue;

    private String status;

    private Boolean onlyUpdate = Boolean.FALSE;

    private Boolean dataStreamTrigger = false;

    private Boolean needWriteFlowable = Boolean.TRUE;

    private List<String> triggerParentList = new ArrayList<>();

    private String source;

}
