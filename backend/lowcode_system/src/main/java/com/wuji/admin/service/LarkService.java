package com.wuji.admin.service;

import com.wuji.admin.model.vo.JsapiAuthVO;

public interface LarkService extends OrganizePullDataService {

    JsapiAuthVO jsapiAuth(String url, String secretId);

}
