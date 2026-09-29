package com.wuji.service.valid;

import com.wuji.common.privilege.resource.AbstractResourceValidator;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.service.FormPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FormValidator extends AbstractResourceValidator {

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Override
    protected boolean validateResources(List<String> resourceIds, String applicationId) {
        String formId = resourceIds.get(0);
        List<FormPrivilegeVO> formPrivilegeVOList =
                formPrivilegeService.getUserPrivilegeByCategory(Collections.singletonList(formId), applicationId);
        return CollectionUtils.isNotEmpty(formPrivilegeVOList);
    }
}
