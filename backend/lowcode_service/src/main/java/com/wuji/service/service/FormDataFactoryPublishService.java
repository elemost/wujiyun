package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormDataFactoryPublishEntity;
import com.wuji.service.model.request.FormDataFactoryPublishRequest;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-01-17
 */
public interface FormDataFactoryPublishService extends IService<FormDataFactoryPublishEntity> {

    void publish(FormDataFactoryPublishRequest formDataFactoryPublishRequest);

    void batchPublish(List<FormDataFactoryPublishRequest> formDataFactoryPublishRequests);

    void updateName(String id, String applicationId, String factoryName);

    FormDataFactoryPublishVO getLastVersion(String applicationId, String id);

    void delete(String applicationId, String id);

    List<FormDataFactoryPublishVO> queryPublishList(String applicationId, List<String> ids);
}
