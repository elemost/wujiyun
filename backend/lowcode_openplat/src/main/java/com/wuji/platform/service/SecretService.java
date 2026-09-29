package com.wuji.platform.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.platform.model.entity.SecretEntity;
import com.wuji.platform.model.request.SecretGenerateRequest;
import com.wuji.platform.model.request.SecretRemarkRequest;
import com.wuji.platform.model.vo.SecretVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-06
 */
public interface SecretService extends IService<SecretEntity> {
    SecretVO getSecret(String appKey);

    void generate(SecretGenerateRequest secretGenerateRequest);

    void delete(String id);

    void updateState(String id, String state);

    void remark(SecretRemarkRequest secretRemarkRequest);

    List<SecretVO> currentCompanyList();
}
