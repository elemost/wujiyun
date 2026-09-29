package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.FormDataStreamPublishEntity;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.request.FormDataStreamButtonTriggerRequest;
import com.wuji.service.model.request.FormDataStreamPublishRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.FormDataStreamPublishVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-26
 */
public interface FormDataStreamPublishService extends IService<FormDataStreamPublishEntity> {
    void publish(FormDataStreamUpdateRequest formDataStreamUpdateRequest);

    void trigger(String formId, String operate, LowcodeDataDomain lowcodeDataDomain, String applicationId,
                 FormDataStreamTrigger formDataStreamTrigger);

    void trigger(String formId, String action, LowcodeDataDomain lowcodeDataDomain, String applicationId,
                 FormDataStreamTrigger formDataStreamTrigger, String dataStreamId);

    void buttonTrigger(FormDataStreamButtonTriggerRequest formDataStreamButtonTriggerRequest);

    void timeTrigger(String dataStreamId, String applicationId);

    void delete(String id, String applicationId);

    void openOrClose(String id, String applicationId, Boolean enable);

    List<FormDataStreamPublishVO> getByApplicationIdList(String applicationId);

    void batchPublish(List<FormDataStreamPublishRequest> formDataStreamPublishRequests);
}
