package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationInfoEntity;
import com.wuji.service.model.request.ApplicationInfoRequest;
import com.wuji.service.model.vo.ApplicationInfoVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-06-03
 */
public interface ApplicationInfoService extends IService<ApplicationInfoEntity> {
    void batchSave(String applicationId, List<ApplicationInfoRequest> applicationInfoList);

    void insert(String applicationId, String infoKey, String infoValue);

    void saveByKey(String applicationId, String infoKey, String infoValue);

    ApplicationInfoVO getByApplicationAndKey(String applicationId, String infoKey);

    List<ApplicationInfoVO> getByApplicationIdList(List<String> applicationIdList);

    List<ApplicationInfoVO> expire(Integer day);
}
