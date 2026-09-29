package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.CompanyAppEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 企业使用产品限制 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@DS("slave")
public interface CompanyAppMapper extends BaseMapper<CompanyAppEntity> {
    @Select(" SELECT * FROM wj_company_app " +
            " WHERE client_id = 'elecloud' and TIMESTAMP(DATE(end_time)) = TIMESTAMP(DATE_ADD(DATE(NOW()), INTERVAL ${day} DAY)) " )
    List<CompanyAppEntity> expire(@Param("day") Integer day);

    @Select(" SELECT * FROM wj_company_app " +
            " WHERE client_id = 'elecloud' and equity_id = 'elecloud_pro_free' and TIMESTAMP(DATE(NOW())) = TIMESTAMP(DATE_ADD(DATE(start_time), INTERVAL ${day} DAY)) " )
    List<CompanyAppEntity> trialExperience(@Param("day") Integer day);

    @Select(" SELECT * FROM wj_company_app " +
            " WHERE client_id = 'elecloud' and equity_id = 'elecloud_pro_free' and TIMESTAMP(DATE(end_time)) = TIMESTAMP(DATE_ADD(DATE(NOW()), INTERVAL ${day} DAY)) " )
    List<CompanyAppEntity> trialExpirationReminder(@Param("day") Integer day);


    @Select(" SELECT * FROM wj_company_app " +
            " WHERE client_id = 'elecloud' and equity_id in (${equityId}) and TIMESTAMP(DATE(end_time)) = TIMESTAMP(DATE_ADD(DATE(NOW()), INTERVAL ${day} DAY)) " )
    List<CompanyAppEntity> versionAboutExpire(@Param("equityId") String equityId, @Param("day") Integer day);

    @Select(" SELECT * FROM wj_company_app " +
            " WHERE client_id = 'elecloud' and equity_id = 'elecloud_pro_free' and TIMESTAMP(DATE(now())) <= TIMESTAMP(DATE_ADD(DATE(start_time), INTERVAL ${day} DAY)) " )
    List<CompanyAppEntity> trialBeenUsedDays(@Param("day") Integer day);

}
