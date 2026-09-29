package com.wuji.common.privilege.resource;

import java.util.List;

public interface ResourceValidator {
    boolean validate(List<String> resourceId, String applicationId);
}
