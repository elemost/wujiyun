package com.wuji.systemapi.service;

import com.wuji.systemapi.client.user.model.FormDataRequest;
import com.wuji.systemapi.client.user.model.LowcodeDataOpenDomain;
import com.wuji.systemapi.client.user.model.TemplateApplicationOpenRequest;

import java.util.List;

public interface TemplateApplicationOpenService {
    String getTemplateInfo(String templateApplicationId);

    List<LowcodeDataOpenDomain> queryList(String templateApplicationId, List<FormDataRequest> formDataRequests);

    String templateInfo(String templateApplicationId);

    String templateList(TemplateApplicationOpenRequest templateApplicationRequest);
}
