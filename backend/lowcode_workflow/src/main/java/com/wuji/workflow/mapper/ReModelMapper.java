package com.wuji.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.workflow.model.entity.ReModelEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2026-05-18
 */
public interface ReModelMapper extends BaseMapper<ReModelEntity> {


    @Select("select * from act_re_model " +
            " ${ew.customSqlSegment} ")
    List<ReModelEntity> getByDeploymentId(@Param("ew") Wrapper<ReModelEntity> queryWrapper);
}
