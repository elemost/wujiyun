package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.CompanyPullConfigEntity;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
@DS("slave")
public interface CompanyPullConfigMapper extends BaseMapper<CompanyPullConfigEntity> {

}
