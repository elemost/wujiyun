package com.wuji.service.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.TemplateApplicationVO;

public interface ApplicationOemService {

    String useTemplate(String templateId, Boolean needData);

    TemplateApplicationVO info(String id);

    QueryPageVO<TemplateApplicationVO> queryList(TemplateApplicationRequest templateApplicationRequest);
}
