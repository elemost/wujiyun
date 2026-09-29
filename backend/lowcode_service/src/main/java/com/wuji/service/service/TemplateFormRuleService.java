package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormRuleEntity;
import com.wuji.service.model.vo.TemplateFormRuleVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-12-31
 */
public interface TemplateFormRuleService extends IService<TemplateFormRuleEntity> {

    void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist);

    List<TemplateFormRuleVO> getByApplicationId(String applicationId);


}
