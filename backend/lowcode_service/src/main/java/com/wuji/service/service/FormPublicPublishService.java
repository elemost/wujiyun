package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormPublicPublishEntity;
import com.wuji.service.model.request.FormPublicPublishSaveRequest;
import com.wuji.service.model.vo.FormPublicPublishVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-10
 */
public interface FormPublicPublishService extends IService<FormPublicPublishEntity> {
    FormPublicPublishVO save(FormPublicPublishSaveRequest formPublicPublishSaveRequest);

    FormPublicPublishVO info(String id);

    FormPublicPublishVO info(String applicationId, String formId, String publishType);

    void checkSecret(String accessToken, String accessType, String formId, String applicationId);

    List<FormPublicPublishVO> getByApplicationId(String applicationId);

    void useTemplate(String templateApplicationId, String applicationId);

    void copyApplication(String applicationId, String sourceApplicationId);
}
