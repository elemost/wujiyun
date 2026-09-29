package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import lombok.Data;

import java.util.List;

@Data
public class WorkFlowBatchAuditRequest {

    @CorpCoop
    private String companyUuid;

    private List<WorkFlowTaskRequest> tasks;
}
