package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormPrivilegeUserEntity;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;

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
public interface TemplateFormPrivilegeUserService extends IService<TemplateFormPrivilegeUserEntity> {
    void generateTemplate(String applicationId, Map<String, String> privilegeIdMap,
                          List<FormPrivilegeUserVO> formPrivilegeUserVOList);

    List<TemplateFormPrivilegeUserVO> getByApplicationId(String applicationId);

    void deleteByApplicationId(String applicationId);
}
