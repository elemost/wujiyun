package com.wuji.common.privilege.resource;

public interface ClientFunctionValidator {
    boolean validate(String applicationId, String clientCode, String clientName);
}
