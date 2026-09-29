package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.ApplicationUseTimeEntity;
import com.wuji.service.model.vo.ApplicationCompanyUseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-09-04
 */
public interface ApplicationUseTimeMapper extends BaseMapper<ApplicationUseTimeEntity> {
    @Select(" select  max(use_time) as lastTime, company_id from lc_application_use_time " +
            " ${ew.customSqlSegment} ")
    List<ApplicationCompanyUseVO> applicationUseTime(@Param("ew") QueryWrapper<ApplicationCompanyUseVO> queryWrapper);
}
