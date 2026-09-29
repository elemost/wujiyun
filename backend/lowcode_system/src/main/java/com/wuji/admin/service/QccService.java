package com.wuji.admin.service;

import com.wuji.admin.client.qcc.model.QccBasicDetailVO;

import java.util.List;

public interface QccService {
    List<QccBasicDetailVO> qccCompany(String companyName);
}
