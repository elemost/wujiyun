package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormSerialNumberEntity;
import com.wuji.service.model.request.FormSerialNumberRequest;
import com.wuji.service.model.request.FormSerialNumberResetRequest;
import com.wuji.service.model.vo.FormSerialNumberVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-01
 */
public interface FormSerialNumberService extends IService<FormSerialNumberEntity> {
    Integer insertOrUpdate(String dateTimeKey, String serialKey, Integer initialValue);

    void reset(FormSerialNumberResetRequest formSerialNumberResetRequest);

    FormSerialNumberVO getInfo(FormSerialNumberRequest formSerialNumberRequest);

    void copyApplication(String applicationId, String sourceApplicationId, Boolean needDate);
}
