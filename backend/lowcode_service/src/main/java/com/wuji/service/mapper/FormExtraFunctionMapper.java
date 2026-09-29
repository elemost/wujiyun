package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
public interface FormExtraFunctionMapper extends BaseMapper<FormExtraFunctionEntity> {

    @Select(" select ifnull(max(sort), 0) from lc_form_extra_function ${ew.customSqlSegment} ")
    Integer maxSort(@Param("ew") QueryWrapper<FormExtraFunctionEntity> queryWrapper);
}
