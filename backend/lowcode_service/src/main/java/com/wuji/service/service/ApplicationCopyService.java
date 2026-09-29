package com.wuji.service.service;

import com.wuji.service.model.request.ApplicationCopyRequest;

public interface ApplicationCopyService {

    String copy(ApplicationCopyRequest applicationCopyRequest);

    String share(ApplicationCopyRequest applicationCopyRequest);

}
