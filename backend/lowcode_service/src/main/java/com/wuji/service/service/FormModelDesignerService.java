package com.wuji.service.service;

import com.wuji.service.model.request.FormModelDesignerRequest;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;

import java.util.List;

public interface FormModelDesignerService {
    String designModel(FormModelDesignerRequest formModelDesignerRequest);

    List<FlowableActivityConfigDomain> getByFormId(String formId, String dataUuid, String applicationId);

    List<FlowableActivityConfigDomain> getPublishModelConfig(String formId,  String applicationId);
}
