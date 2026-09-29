package com.wuji.service.valid;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.privilege.resource.AbstractResourceValidator;
import com.wuji.service.cache.FormPublicPublishCache;
import com.wuji.service.model.info.FormPublicPublishConfig;
import com.wuji.service.model.vo.FormPublicPublishVO;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class FormPublicPublishValidator extends AbstractResourceValidator {

    @Override
    protected boolean validateResources(List<String> resourceIds, String applicationId) {
        String resourceId = resourceIds.get(0);
        FormPublicPublishVO config = FormPublicPublishCache.getConfig(resourceId + "_" + applicationId);
        if (config == null) {
            return false;
        }
        if (config.getState() == 0) {
            return false;
        }
        FormPublicPublishConfig formPublicPublishConfig =
                JSONObject.parseObject(config.getConfig(), FormPublicPublishConfig.class);
        if ("custom".equals(formPublicPublishConfig.getInDateLimit())) {
            return new Date().after(formPublicPublishConfig.getStartTime()) &&
                    formPublicPublishConfig.getEndTime().after(new Date());
        }
        return true;
    }

}
