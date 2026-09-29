package com.wuji.admin.service.impl;

import com.wuji.admin.client.qcc.QccClient;
import com.wuji.admin.client.qcc.QccResult;
import com.wuji.admin.client.qcc.model.FuzzySearchVO;
import com.wuji.admin.client.qcc.model.QccBasicDetailVO;
import com.wuji.admin.service.QccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QccServiceImpl implements QccService {

    @Autowired
    private QccClient qccClient;

    @Override
    public List<QccBasicDetailVO> qccCompany(String companyName) {
        QccResult<FuzzySearchVO> listQccResult = qccClient.fuzzySearch(companyName);
        FuzzySearchVO result = listQccResult.getResult();
        if (result == null) {
            return new ArrayList<>();
        }
        return result.getList();
    }
}
