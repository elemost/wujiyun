package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.UserPostEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户与岗位关联表 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
@DS("slave")
@Repository
public interface UserPostMapper extends BaseMapper<UserPostEntity> {

}
