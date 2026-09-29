package com.wuji.admin.service;

import com.wuji.admin.model.request.WeComSignatureRequest;
import com.wuji.admin.model.vo.WeComSignatureVO;

public interface WeComCommonService extends OrganizePullDataService {
    WeComSignatureVO getCompanySignature(WeComSignatureRequest weComSignatureRequest);

    WeComSignatureVO geAppSignature(WeComSignatureRequest weComSignatureRequest);
}
