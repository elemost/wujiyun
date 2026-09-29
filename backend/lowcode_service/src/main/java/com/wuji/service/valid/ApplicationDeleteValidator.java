package com.wuji.service.valid;

import com.wuji.common.privilege.resource.AbstractResourceValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.ManageApplicationVO;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ApplicationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ApplicationDeleteValidator extends AbstractResourceValidator {

    @Autowired
    private ApplicationService applicationService;


    @Override
    protected boolean validateResources(List<String> resourceIds, String applicationId) {
        ApplicationVO applicationVO = applicationService.detail(applicationId);
        if (applicationVO == null) {
            return false;
        }
        if (UserUtils.getUser().getUserId().equals(applicationVO.getCreator())) {
            return true;
        }
        if (!UserUtils.getUser().getAdminUser()) {
            ManageVO config =
                    ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
            if (config == null) {
                return false;
            }
            if (config.getSuperManage()) {
                return true;
            }

            if (CollectionUtils.isEmpty(config.getManageApplicationList())) {
                return false;
            }
            if (!config.getManageApplicationList().stream().map(ManageApplicationVO::getApplicationId)
                    .collect(Collectors.toList()).contains(applicationId)) {
                return false;
            }
        }
        return Objects.equals(UserUtils.getUser().getCompanyId(), applicationVO.getCompanyId());
    }
}
