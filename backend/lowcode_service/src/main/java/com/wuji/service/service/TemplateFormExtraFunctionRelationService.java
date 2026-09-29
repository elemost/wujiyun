package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateFormExtraFunctionRelationEntity;
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
 * @since 2025-01-20
 */
public interface TemplateFormExtraFunctionRelationService extends IService<TemplateFormExtraFunctionRelationEntity> {
    void generateTemplate(Map<String, String> functionIdMap,
                          List<FormExtraFunctionRelationVO> formExtraFunctionRelationList,
                          Map<String, String> privilegeIdMap);

    List<TemplateFormExtraFunctionRelationVO> getByFunctionIdList(List<String> idList);
}
