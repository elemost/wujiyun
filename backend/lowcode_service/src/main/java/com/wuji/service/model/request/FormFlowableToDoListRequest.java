package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormFlowableToDoListRequest extends FormFlowableQueryCommonRequest {
    private String taskName;

    private String processName;

    private String applicationId;

    private String taskId;

    @CorpCoop
    private String companyUuid;

    private List<String> createUsers;

    private String keyword;
}
