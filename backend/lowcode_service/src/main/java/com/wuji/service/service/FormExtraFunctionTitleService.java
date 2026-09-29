package com.wuji.service.service;

import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.request.FormExtraFunctionTitleSaveRequest;

import java.util.List;

public interface FormExtraFunctionTitleService extends FormExtraFunctionService {
    void buildTitle(List<LowcodeDataDomain> lowcodeDataDomainList, String config, String formId, String applicationId);

    void saveTitle(String config, String formId, String applicationId);

    void saveTitleConfig(FormExtraFunctionTitleSaveRequest formExtraFunctionTitleSaveRequest);
}
