package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CompanyPullConfigEntity;
import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.common.model.vo.CompanyPullConfigVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
public interface CompanyPullConfigService extends IService<CompanyPullConfigEntity> {
    void save(CompanyPullConfigSaveRequest companyPullConfigSaveRequest);

    void weComAuth(CompanyPullConfigSaveRequest companyPullConfigSaveRequest);

    List<CompanyPullConfigVO> getPullConfig();

    CompanyPullConfigVO getByType(Long companyId, String configType);

    CompanyPullConfigVO getByCompanyIdAndSuiteId(Long companyId, String suiteId);
}
