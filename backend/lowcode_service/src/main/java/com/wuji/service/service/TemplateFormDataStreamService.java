package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormDataStreamEntity;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-04-09
 */
public interface TemplateFormDataStreamService extends IService<TemplateFormDataStreamEntity> {
    void generateTemplate(String applicationId, String templateApplicationId, List<String> formIdList, Boolean exist);

    List<TemplateFormDataStreamVO> getByApplicationId(String applicationId);
}
