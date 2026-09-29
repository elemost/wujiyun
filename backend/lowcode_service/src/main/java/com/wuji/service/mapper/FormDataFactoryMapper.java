package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2026-01-05
 */
public interface FormDataFactoryMapper extends BaseMapper<FormDataFactoryEntity> {
    @Select(" select count(0) as count,application_id from lc_form_data_factory " +
            " ${ew.customSqlSegment} ")
    List<FormDataFactoryStatisticVO> applicationStatistic(
            @Param("ew") QueryWrapper<FormDataFactoryEntity> queryWrapper);
}
