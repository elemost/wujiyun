package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormAggregateEntity;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 聚合表 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-03-10
 */
public interface FormAggregateMapper extends BaseMapper<FormAggregateEntity> {

    @Select(" select count(0) as count,application_id from lc_form_aggregate " +
            " ${ew.customSqlSegment} ")
    List<FormAggregateStatisticVO> applicationStatistic(
            @Param("ew") QueryWrapper<FormAggregateEntity> queryWrapper);
}
