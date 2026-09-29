package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormPublicPublishEntity;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-24
 */
public interface TemplateFormPublicPublishService extends IService<TemplateFormPublicPublishEntity> {
    void generateTemplate(String applicationId, String templateApplicationId, Boolean exist);

    List<TemplateFormPublicPublishVO> getByApplicationId(String applicationId);
}
