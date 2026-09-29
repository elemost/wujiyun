package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.model.entity.DepartmentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
@Repository
@DS("slave")
public interface DepartmentMapper extends BaseMapper<DepartmentEntity> {

}
