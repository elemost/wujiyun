package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormAggregateEntity;
import com.wuji.service.model.vo.TemplateFormAggregateVO;

import java.util.List;

/**
 * <p>
 * 聚合表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
public interface TemplateFormAggregateService extends IService<TemplateFormAggregateEntity> {
    void generateTemplate(String applicationId,  String templateApplicationId , Boolean exist);

    List<TemplateFormAggregateVO> getByApplicationId(String id);
}
