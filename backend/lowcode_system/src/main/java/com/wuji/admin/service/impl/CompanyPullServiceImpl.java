package com.wuji.admin.service.impl;

import com.wuji.admin.handler.PullDataContext;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyPullService;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyPullServiceImpl implements CompanyPullService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PullDataContext pullDataContext;

    @Override
    public void pullThirdId() {
        UserDomain user = UserUtils.getUser();
        CompanyVO info = companyService.info(user.getCompanyId());
        String dataSource = info.getDataSource();
        pullDataContext.getHandler(dataSource).pullThirdId();
    }
}
