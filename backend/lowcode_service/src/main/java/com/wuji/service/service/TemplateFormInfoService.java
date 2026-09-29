package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormInfoEntity;
import com.wuji.service.model.vo.TemplateFormInfoVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
public interface TemplateFormInfoService extends IService<TemplateFormInfoEntity> {
    void generateTemplate(String sourceApplicationId, String templateApplicationId, Boolean exist);

    List<TemplateFormInfoVO> getByApplicationId(String applicationId);
}
