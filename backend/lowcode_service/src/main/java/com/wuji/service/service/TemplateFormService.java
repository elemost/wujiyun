package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormEntity;
import com.wuji.service.model.vo.TemplateFormVO;

import java.util.List;

/**
 * <p>
 * 表单 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
public interface TemplateFormService extends IService<TemplateFormEntity> {
    void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist,
                          List<String> formIdList);

    void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId, Boolean needData);

    List<TemplateFormVO> getByIdList(List<String> idList, String templateApplicationId);

    List<TemplateFormVO> getByApplicationId(String templateApplicationId);
}
