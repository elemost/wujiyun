package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.TemplateApplicationVO;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
public interface TemplateApplicationService extends IService<TemplateApplicationEntity> {
    String generateTemplate(String applicationId);

    String generateTemplateByTemplateId(String templateId);

    String useTemplate(String templateApplicationId, Boolean needData);

    QueryPageVO<TemplateApplicationVO> queryList(TemplateApplicationRequest templateApplicationRequest);

    TemplateApplicationVO info(String applicationId);

    void dealIcon();
}
