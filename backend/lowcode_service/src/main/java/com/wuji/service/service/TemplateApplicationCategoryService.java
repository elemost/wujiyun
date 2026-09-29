package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;

/**
 * <p>
 * 应用目录 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
public interface TemplateApplicationCategoryService extends IService<TemplateApplicationCategoryEntity> {
    void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist);

    void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId, Boolean needDate);
}
