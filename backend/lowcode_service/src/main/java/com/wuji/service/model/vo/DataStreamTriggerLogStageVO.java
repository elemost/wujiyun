package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormDataTitle;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamTriggerLogStageVO {
    private String triggerUuid;

    private Long nodeId;

    private Long createTime;

    private String creator;

    private String createName;

    private List<FormDataTitle> resultList;

    private List<FormDataTitle> insertList;

    private List<FormDataTitle> updateList;

    private List<FormDataTitle> deleteList;

    private Object calculateResult;

    private String action;

    private String nodeType;

    private String businessId;

    private String businessType;

    private String applicationId;

    private Object result;

    private String errorMessage;
}
