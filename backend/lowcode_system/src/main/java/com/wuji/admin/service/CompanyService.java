package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CompanyEntity;
import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.admin.model.request.CompanyRequest;
import com.wuji.admin.model.request.CompanySaveRequest;
import com.wuji.admin.model.vo.CompanyVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
public interface CompanyService extends IService<CompanyEntity> {
    List<CompanyVO> getAllList();

    CompanyVO info(Long companyId);

    CompanyVO infoByUuid(String companyUuid);

    CompanyVO getBySecretId(String secretId);

    void savePullConfig(CompanyPullConfigSaveRequest companyPullConfigSaveRequest);

    List<CompanyVO> userCompany();

    void update(CompanySaveRequest companySaveRequest);

    Long createCompany(String companyName, String parentUuid, String channelType);

    Map<String, CompanyVO> saveBatchCompany(List<String> companyNameList);

    void createCompany(CompanyRequest companyRequest);

    void checkCompanyExist(String companyName);

    List<CompanyVO> getByIds(List<Long> companyIdList);

    CompanyVO getByCompanyName(String companyName, String channelType);

    List<CompanyVO> getByCompanyNames(List<String> companyNameList, String channelType);

    String getPullConfig(String companyUuid);

    Long initCompany();
}
