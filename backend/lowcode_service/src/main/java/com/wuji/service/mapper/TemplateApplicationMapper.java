package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuji.service.model.domain.ApplicationDomain;
import com.wuji.service.model.domain.TemplateApplicationDomain;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应用 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Repository
public interface TemplateApplicationMapper extends BaseMapper<TemplateApplicationEntity> {

    @Select(" SELECT ta.id, ta.application_name, ta.logo, ta.download_count,ta.description, ta.introduce,ta.icon FROM lc_template_application ta " +
            " LEFT JOIN lc_template_application_tag tat on ta.id = tat.application_id  " + " ${ew.customSqlSegment} ")
    IPage<TemplateApplicationDomain> selectPages(IPage<ApplicationDomain> page,
                                                 @Param("ew") QueryWrapper<TemplateApplicationDomain> queryWrapper);
}
