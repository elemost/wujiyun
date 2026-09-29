package com.wuji.workflow.model.domain;


import com.wuji.workflow.model.flowable.model.Node;
import lombok.Data;

import java.util.List;

@Data
public class FormModelDesignerDomain {
    private Node process;

    private String graphConfig;

    private List<FlowableActivityConfigDomain> flowableTaskConfigList;
}
