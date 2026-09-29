package com.wuji.service.model.domain;

import com.wuji.service.model.info.FormDataTitle;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "data_stream_trigger_log_stage")
@Data
public class DataStreamTriggerLogStageDomain {
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

    private Integer cycleIndex;
}
