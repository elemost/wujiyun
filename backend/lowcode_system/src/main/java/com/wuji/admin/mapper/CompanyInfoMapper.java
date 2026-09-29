package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.CompanyInfoEntity;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-04-07
 */
@DS("slave")
public interface CompanyInfoMapper extends BaseMapper<CompanyInfoEntity> {

}
