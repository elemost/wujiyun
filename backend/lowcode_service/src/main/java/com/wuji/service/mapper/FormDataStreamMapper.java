package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-12-30
 */
public interface FormDataStreamMapper extends BaseMapper<FormDataStreamEntity> {
    @Select(" select count(0) as count,application_id from lc_form_data_stream " +
            " ${ew.customSqlSegment} ")
    List<FormDataStreamStatisticVO> applicationStatistic(
            @Param("ew") QueryWrapper<FormDataStreamEntity> queryWrapper);
}
