package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.model.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
@Repository
@DS("slave")
public interface RoleMapper extends BaseMapper<RoleEntity> {

}
