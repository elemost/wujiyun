package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.ClientFunctionEntity;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@DS("slave")
public interface ClientFunctionMapper extends BaseMapper<ClientFunctionEntity> {

}
