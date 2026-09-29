package com.wuji.systemapi.service.impl;

import com.wuji.common.utils.UserUtils;
import com.wuji.systemapi.client.user.SystemClient;
import com.wuji.systemapi.client.user.SystemResult;
import com.wuji.systemapi.client.user.model.FormDataRequest;
import com.wuji.systemapi.client.user.model.LowcodeDataOpenDomain;
import com.wuji.systemapi.client.user.model.TemplateApplicationOpenRequest;
import com.wuji.systemapi.service.TemplateApplicationOpenService;
import com.wuji.systemapi.utils.DataEncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateApplicationOpenOpenServiceImpl implements TemplateApplicationOpenService {

    @Autowired
    private SystemClient systemClient;

    @Override
    public String getTemplateInfo(String templateApplicationId) {
        SystemResult<String> templateInfo = systemClient.getTemplateInfo(templateApplicationId);
        return DataEncryptUtils.decrypt(UserUtils.getUser().getCompanyUuid(), templateInfo.getData());
    }

    @Override
    public List<LowcodeDataOpenDomain> queryList(String templateApplicationId, List<FormDataRequest> formDataRequests) {
        SystemResult<List<LowcodeDataOpenDomain>> templateData =
                systemClient.getTemplateInfo(formDataRequests, templateApplicationId);
        return templateData.getData();
    }

    @Override
    public String templateInfo(String templateApplicationId) {
        SystemResult<String> templateInfo = systemClient.templateInfo(templateApplicationId);
        return DataEncryptUtils.decrypt(UserUtils.getUser().getCompanyUuid(), templateInfo.getData());
    }

    @Override
    public String templateList(TemplateApplicationOpenRequest templateApplicationRequest) {
        SystemResult<String> stringSystemResult = systemClient.templateList(templateApplicationRequest);
        return DataEncryptUtils.decrypt(UserUtils.getUser().getCompanyUuid(), stringSystemResult.getData());
    }
}
