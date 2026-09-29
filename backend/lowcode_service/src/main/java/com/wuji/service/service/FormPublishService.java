package com.wuji.service.service;

import com.wuji.service.model.entity.FormPublishEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.request.FormPublishPublishRequest;
import com.wuji.service.model.vo.FormPublishVO;

/**
 * <p>
 * 表单发布表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-08-20
 */
public interface FormPublishService extends IService<FormPublishEntity> {
    void publish(FormPublishPublishRequest formPublishPublishRequest);

    FormPublishVO info(String categoryId, Integer version);

    void updateByCategory(String categoryId, String config);
}
