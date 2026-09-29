package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import lombok.Data;

@Data
public class FormFlowableDoneListRequest extends FormFlowableQueryCommonRequest {
    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;


    private String applicationId;


    private String taskId;

    @CorpCoop
    private String companyUuid;
}
