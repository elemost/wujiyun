package com.wuji.service.service;

import com.wuji.service.model.info.SerialNumberConfig;

import java.util.List;

public interface SerialNumberService {
    String getSerialNumber(List<SerialNumberConfig> configList, String fieldId, String formId, String applicationId);
}
