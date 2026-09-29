package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormInfoEntity;
import com.wuji.service.model.request.FormInfoCopyRequest;
import com.wuji.service.model.request.FormInfoCreateRequest;
import com.wuji.service.model.request.FormInfoRequest;
import com.wuji.service.model.request.FormInfoSortRequest;
import com.wuji.service.model.request.FormInfoUpdateRequest;
import com.wuji.service.model.vo.FormInfoVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
public interface FormInfoService extends IService<FormInfoEntity> {
    String create(FormInfoCreateRequest formInfoCreateRequest);

    void update(FormInfoUpdateRequest formInfoUpdateRequest);

    FormInfoVO info(String id, String applicationId, String formId);

    void delete(String id, String applicationId, String formId);

    void setDefault(String id, String applicationId, String formId, Boolean defaultConfig);

    void setEnable(String id, String applicationId, String formId, Boolean enable);

    List<FormInfoVO> queryList(FormInfoRequest formInfoRequest);

    FormInfoVO getDefault(String applicationId, String formId);

    List<FormInfoVO> queryByApplicationId(String applicationId);

    void userTemplate(String applicationId, String templateApplicationId);

    void copyApplication(String applicationId, String sourceApplicationId);

    void sort(FormInfoSortRequest formInfoSortRequest);

    String copy(FormInfoCopyRequest formInfoCopyRequest);

    void copy(String formId, String newFormId, String applicationId);
}
