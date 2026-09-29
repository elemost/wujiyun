package com.wuji.admin.service;

import com.wuji.admin.model.vo.JsapiAuthVO;

public interface DingTalkService extends OrganizePullDataService {
    JsapiAuthVO jsapiAuth(String secretId);
}
