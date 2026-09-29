package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FormFlowableOwnerListRequest extends FormFlowableQueryCommonRequest {
    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    private String applicationId;

    private String processInstanceId;

    @CorpCoop
    private String companyUuid;


    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
