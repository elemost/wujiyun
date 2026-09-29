package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormModuleEntity;
import com.wuji.service.model.vo.TemplateFormModuleVO;

import java.util.List;

/**
 * <p>
 * 表单组件表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-01-21
 */
public interface TemplateFormModuleService extends IService<TemplateFormModuleEntity> {
    void generateTemplate(String applicationId, String templateApplicationId, Boolean exist);

    List<TemplateFormModuleVO> getByApplicationId(String applicationId);
}
