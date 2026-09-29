package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.VerificationCodeEntity;

/**
 * <p>
 * 验证码 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
public interface VerificationCodeService extends IService<VerificationCodeEntity> {
    void insert(String mobile, String code, Long createTime);
}
