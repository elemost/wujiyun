package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.VerificationCodeMapper;
import com.wuji.admin.model.entity.VerificationCodeEntity;
import com.wuji.admin.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 验证码 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Service
@DS("slave")
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCodeEntity>
        implements VerificationCodeService {

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Override
    public void insert(String mobile, String code, Long createTime) {
        VerificationCodeEntity verificationCode = new VerificationCodeEntity();
        verificationCode.setCode(code);
        verificationCode.setMobile(mobile);
        verificationCode.setCodeUsed(false);
        verificationCode.setCreateTime(createTime);
        verificationCodeMapper.insert(verificationCode);
    }
}
