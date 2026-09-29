package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CompanyAppEntity;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.model.vo.CompanyAppVO;

import java.util.List;

/**
 * <p>
 * 企业使用产品限制 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
public interface CompanyAppService extends IService<CompanyAppEntity> {
    CompanyAppDetailVO getCurrent();

    CompanyAppDetailVO getCurrentClient();

    void saveDefault(Long companyId);

    void expire();

    List<CompanyAppVO> beenUsedDays(int day);

    List<CompanyAppVO> getByCompanyId(List<Long> companyIdList);

    CompanyAppVO getCurrentInfo();

    void updateVersion(String config, Integer orderPeriod);
}
