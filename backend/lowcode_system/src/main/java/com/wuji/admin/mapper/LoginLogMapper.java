package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.LoginLogEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 登录日志 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-05-28
 */
@DS("slave")
@Repository
public interface LoginLogMapper extends BaseMapper<LoginLogEntity> {

}
