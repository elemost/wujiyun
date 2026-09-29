package com.wuji.service.valid;

import com.wuji.common.privilege.resource.AbstractFunctionValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.model.vo.ManageVO;
import org.springframework.stereotype.Component;

@Component
public class ManageValidator extends AbstractFunctionValidator {

    @Override
    protected boolean validateResources(String applicationId, String clientCode, String clientName) {
        if (!UserUtils.getUser().getAdminUser()) {
            ManageVO config =
                    ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
            if (config == null) {
                return false;
            }
            return config.getSuperManage();
        } else {
            return true;
        }
    }
}
