package com.wuji.common.privilege.resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFunctionValidator implements ClientFunctionValidator {
    @Override
    public boolean validate(String applicationId, String clientCode, String clientName) {
        return validateResources(applicationId, clientCode, clientName);
    }

    protected abstract boolean validateResources(String applicationId, String clientCode,  String clientName);

}
