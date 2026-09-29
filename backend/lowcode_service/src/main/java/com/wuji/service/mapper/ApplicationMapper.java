package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuji.service.model.domain.ApplicationDomain;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.vo.ApplicationCountVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应用 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@Repository
public interface ApplicationMapper extends BaseMapper<ApplicationEntity> {
    @Select(" SELECT a.* FROM lc_application a LEFT JOIN lc_application_privilege ap on a.id = ap.application_id " +
            " ${ew.customSqlSegment} ")
    IPage<ApplicationDomain> selectPages(IPage<ApplicationDomain> page,
                                         @Param("ew") QueryWrapper<ApplicationDomain> queryWrapper);

    @Select(" SELECT a.* FROM lc_application a LEFT JOIN lc_application_privilege ap on a.id = ap.application_id " +
            " LEFT JOIN lc_application_use_time aut on a.id = aut.application_id " +
            " ${ew.customSqlSegment} ")
    List<ApplicationDomain> getLatestApplicationPrivilege(@Param("ew") QueryWrapper<ApplicationDomain> queryWrapper);

    @Select(" SELECT a.* FROM lc_application a LEFT JOIN lc_application_privilege ap on a.id = ap.application_id " +
            " ${ew.customSqlSegment} ")
    List<ApplicationDomain> selectLists(@Param("ew") QueryWrapper<ApplicationDomain> queryWrapper);

    @Select(" select count(0) as count, company_id from lc_application " +
            " ${ew.customSqlSegment} ")
    List<ApplicationCountVO> applicationCount(@Param("ew") QueryWrapper<ApplicationCountVO> queryWrapper);
}
