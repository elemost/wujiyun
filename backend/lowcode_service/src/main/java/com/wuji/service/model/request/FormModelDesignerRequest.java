package com.wuji.service.model.request;

import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.flowable.model.Node;
import lombok.Data;

import java.util.List;

@Data
public class FormModelDesignerRequest {
    private String modelId;

    private Node process;

    private String graphConfig;

    private List<FlowableActivityConfigDomain> flowableTaskConfigList;

}
