package com.wuji.open.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.open.model.request.FormDataQueryRequest;
import com.wuji.open.model.request.FormSyncOpenRequest;
import com.wuji.open.model.vo.FormDataSyncVO;
import com.wuji.service.model.vo.LowcodeDataVO;

public interface FormOpenService {
    FormDataSyncVO insert(FormSyncOpenRequest formSyncOpenRequest);

    FormDataSyncVO update(FormSyncOpenRequest formSyncOpenRequest);

    FormDataSyncVO delete(FormSyncOpenRequest formSyncOpenRequest);

    QueryPageVO<LowcodeDataVO> queryList(FormDataQueryRequest formDataQueryRequest);
}
