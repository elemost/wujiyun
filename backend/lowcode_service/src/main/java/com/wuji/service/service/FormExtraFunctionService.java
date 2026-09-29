package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.info.FormExtraFunctionButtonAction;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionSortRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.model.vo.FormExtraFunctionVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
public interface FormExtraFunctionService extends IService<FormExtraFunctionEntity> {
    String create(FormExtraFunctionCreateRequest formExtraFunctionCreateRequest);

    String save(FormExtraFunctionCreateRequest formExtraFunctionCreateRequest);

    void update(FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest);

    FormExtraFunctionVO info(String id);

    List<FormExtraFunctionVO> getByFormId(String formId, String applicationId, String functionType);

    List<FormExtraFunctionVO> getByFormIdAndGroupId(String applicationId, String formId, List<String> groupIds);

    void delete(String id);

    List<FormExtraFunctionVO> getByApplicationId(String applicationId);

    void useTemplate(String applicationId, String templateApplicationId, Map<String, String> categoryToPrivilegeMap);

    void useTemplateDefaultPrivilege(String applicationId, String templateApplicationId,
                                     Map<String, String> categoryToPrivilegeMap);

    void generateQrcode(HttpServletResponse httpServletResponse, String id, String dataUuid);

    List<FormExtraFunctionButtonAction> getActions(String id, String applicationId, String formId);

    void sort(FormExtraFunctionSortRequest formExtraFunctionSortRequest);

    void copy(String formId, String newFormId, String applicationId, Map<String, String> privilegeMap,
              Map<String, String> dataStreamIdMap);

    void copyApplication(String applicationId, String sourceApplicationId, Map<String, String> privilegeMap, Boolean share);
}
