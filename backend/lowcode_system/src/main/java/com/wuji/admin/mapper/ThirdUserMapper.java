package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.ThirdUserEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@DS("slave")
@Repository
public interface ThirdUserMapper extends BaseMapper<ThirdUserEntity> {

}
