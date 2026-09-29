package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.request.FormModelCreateRequest;
import com.wuji.service.model.request.FormModelUpdateRequest;
import com.wuji.service.model.request.FormModelUpdateStatusRequest;
import com.wuji.service.model.vo.FormModelVO;

import java.util.List;

/**
 * <p>
 * 流程表单绑定表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-03
 */
public interface FormModelService extends IService<FormModelEntity> {

    FormModelVO createModel(FormModelCreateRequest formModelCreateRequest);

    void updateModel(FormModelUpdateRequest formModelUpdateRequest);

    FormModelVO info(String formId, String applicationId);

    void publishOrBind(String modelId, String formId, String applicationId);

    /**
     * 新版本
     *
     * @return
     */
    String newVersion(String modelId);

    List<FormModelVO> getByFormIdList(List<String> formIdList, String applicationId);

    List<FormModelVO> getByApplicationId(String applicationId);

    void updateStatus(FormModelUpdateStatusRequest formModelUpdateStatusRequest);

    void copyApplication(String applicationId, String sourceApplicationId,
                         List<ApplicationCategoryEntity> flowableList);

    FormModelVO getByFormId(String formId, String applicationId);

    List<FormModelVO> getByBusinessTypes(List<String> businessTypes);
}
