package com.wuji.systemapi.service.impl;

import com.wuji.common.utils.UserUtils;
import com.wuji.systemapi.client.user.SystemClient;
import com.wuji.systemapi.client.user.SystemResult;
import com.wuji.systemapi.service.CompanyAppOpenService;
import com.wuji.systemapi.utils.DataEncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyAppOpenServiceImpl implements CompanyAppOpenService {

    @Autowired
    private SystemClient systemClient;
    @Override
    public String getCompanyAppDetail() {
        SystemResult<String> current = systemClient.getCurrent();
        String data = current.getData();
        return DataEncryptUtils.decrypt(UserUtils.getUser().getCompanyUuid(), data);
    }
}
