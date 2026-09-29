package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.VerificationCodeEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 验证码 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Repository
@DS("slave")
public interface VerificationCodeMapper extends BaseMapper<VerificationCodeEntity> {

}
