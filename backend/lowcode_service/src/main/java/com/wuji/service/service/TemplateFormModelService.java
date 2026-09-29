package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;
import com.wuji.service.model.entity.TemplateFormModelEntity;
import com.wuji.service.model.vo.ApplicationCategoryVO;

import java.util.List;

/**
 * <p>
 * 流程表单绑定表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
public interface TemplateFormModelService extends IService<TemplateFormModelEntity> {
    void generateTemplate(String templateApplicationId, String sourceTemplateId,
                          List<ApplicationCategoryVO> oldApplicationCategoryVOList, Boolean exist);

    void useTemplate(String applicationId, String templateApplicationId,
                     List<TemplateApplicationCategoryEntity> oldTemplateApplicationCategoryVOList);
}
