package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationUseTimeEntity;
import com.wuji.service.model.vo.ApplicationCompanyUseVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-04
 */
public interface ApplicationUseTimeService extends IService<ApplicationUseTimeEntity> {
    void save(String applicationId);

    List<String> getLatestApplication(Integer limitCount);

    void deleteByApplication(String  applicationId);

    List<ApplicationCompanyUseVO> applicationCompanyUse(List<Long> companyIdList);
}
