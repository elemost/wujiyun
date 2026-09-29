package com.wuji.service.components;


import com.wuji.admin.components.CorpCoopComponent;
import com.wuji.service.cache.ApplicationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCorpCoopComponent {

    @Autowired
    private CorpCoopComponent corpCoopComponent;

    public void exchangeCompany(String applicationId) {
        Long companyId = ApplicationCache.getCompanyId(applicationId);
        corpCoopComponent.exchangeCompanyId(companyId);
    }
}
