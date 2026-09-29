package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormDataFactoryEntity;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-02-03
 */
public interface TemplateFormDataFactoryService extends IService<TemplateFormDataFactoryEntity> {
    void generateTemplate(String applicationId,  String templateApplicationId , Boolean exist);

    List<TemplateFormDataFactoryVO> getByApplicationId(String applicationId);

}
