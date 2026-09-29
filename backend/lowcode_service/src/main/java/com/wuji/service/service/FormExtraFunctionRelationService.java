package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormExtraFunctionRelationEntity;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-25
 */
public interface FormExtraFunctionRelationService extends IService<FormExtraFunctionRelationEntity> {
    void save(String functionId, String businessType, List<String> businessIdList);

    List<FormExtraFunctionRelationVO> getByFunctionIdList(List<String> functionIdList, String applicationId);

    List<FormExtraFunctionRelationVO> getByBusinessId(String businessId, String businessType);

    List<FormExtraFunctionRelationVO> getByBusinessIds(List<String> businessIds, String businessType);

    void copy(Map<String, String> functionIdMap, Map<String, String> privilegeMap);

    void useTemplate(Map<String, String> functionIdMap, Map<String, String> privilegeMap);

    void useTemplateDefault(List<TemplateFormExtraFunctionRelationVO> formExtraFunctionRelationList);

    void saveByButton(String privilegeId, List<String> functionIdList);

    List<String> getByPrivilegeId(String privilegeId);
}
