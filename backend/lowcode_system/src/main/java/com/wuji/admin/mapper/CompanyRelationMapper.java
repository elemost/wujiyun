package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.CompanyRelationEntity;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
@DS("slave")
public interface CompanyRelationMapper extends BaseMapper<CompanyRelationEntity> {

}
