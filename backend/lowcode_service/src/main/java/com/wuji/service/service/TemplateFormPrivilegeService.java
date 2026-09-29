package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormPrivilegeEntity;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-01-20
 */
public interface TemplateFormPrivilegeService extends IService<TemplateFormPrivilegeEntity> {
    Map<String, String> generateTemplate(String applicationId, String templateApplicationId, List<String> formIdList, Boolean exist);

    List<TemplateFormPrivilegeVO> getByApplicationId(String applicationId);
}
