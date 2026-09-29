package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormExtraFunctionEntity;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-01-20
 */
public interface TemplateFormExtraFunctionService extends IService<TemplateFormExtraFunctionEntity> {
    void generateTemplate(String applicationId, String templateApplicationId, Map<String, String> privilegeIdMap,
                          Boolean exist);

    List<TemplateFormExtraFunctionVO> getByApplicationId(String applicationId);
}
