package com.wuji.service.valid;

import com.wuji.common.privilege.resource.AbstractResourceValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.components.ApplicationCorpCoopComponent;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.model.vo.ApplicationInfoVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationInfoService;
import com.wuji.service.service.ApplicationPrivilegeService;
import com.wuji.service.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class ApplicationValidator extends AbstractResourceValidator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private ApplicationPrivilegeService applicationPrivilegeService;

    @Autowired
    private ApplicationCorpCoopComponent applicationCorpCoopComponent;

    @Override
    protected boolean validateResources(List<String> resourceIds, String applicationId) {
        String resourceId = resourceIds.get(0);
        applicationCorpCoopComponent.exchangeCompany(resourceId);
        ApplicationVO applicationVO = applicationService.detail(resourceId);
        if (applicationVO == null) {
            return false;
        }
        List<Long> scope = applicationPrivilegeService.getScope(resourceId);
        if (scope.contains(UserUtils.getUser().getUserIdLongValue())) {
            return true;
        }
        ApplicationInfoVO applicationInfoVO =
                applicationInfoService.getByApplicationAndKey(resourceId, ApplicationInfoKeyEnum.EXPIRE_TIME.name());
        if (applicationInfoVO != null && Long.parseLong(applicationInfoVO.getInfoValue()) < new Date().getTime()) {
            return false;
        }
        return Objects.equals(UserUtils.getUser().getCompanyId(), applicationVO.getCompanyId());
    }
}
