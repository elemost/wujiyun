package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormQuoteEntity;
import com.wuji.service.model.vo.TemplateFormQuoteVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-01-21
 */
public interface TemplateFormQuoteService extends IService<TemplateFormQuoteEntity> {
    void generateTemplate(String applicationId, String templateApplicationId, List<String> formIdList, Boolean exist);

    List<TemplateFormQuoteVO> getByApplicationId(String applicationId);
}
