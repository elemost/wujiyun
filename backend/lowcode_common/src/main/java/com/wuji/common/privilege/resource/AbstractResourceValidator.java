package com.wuji.common.privilege.resource;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractResourceValidator implements ResourceValidator {
    @Override
    public boolean validate(List<String> resourceId, String applicationId) {
        return validateResources(resourceId, applicationId);
    }

    protected abstract boolean validateResources(List<String> resourceIds, String applicationId);
}
