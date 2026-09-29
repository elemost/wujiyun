package com.wuji.service.service;

public interface ApplicationDataService {
    void clearAllData(String applicationId);

    void clearAllData(String applicationId, String formId);

    void migrateData(String applicationId);
}
