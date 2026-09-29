package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CompanyInfoEntity;
import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.model.vo.CompanyInfoVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-04-07
 */
public interface CompanyInfoService extends IService<CompanyInfoEntity> {
    void save(Long companyId, CompanyInfoSaveRequest companyInfoSaveRequest);

    void save(List<CompanyInfoSaveRequest> companyInfoSaveRequest, List<String> keyList);

    List<CompanyInfoVO> getByCompanyId(Long companyId);

    CompanyInfoVO getByKey(Long companyId, String key);

    CompanyInfoVO getByKeyAndValue(String key, String value);

    void saveDefaultKey(Long companyId, String clientId, String equityId);

    void saveCompanyInfoWhileOrder(Map<String, Integer> buyMap, String clientId, String equityId, Long companyId);
}
