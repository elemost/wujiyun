package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.common.model.entity.ConfigEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-05-09
 */
@DS("slave")
@Repository
public interface ConfigMapper extends BaseMapper<ConfigEntity> {

}
