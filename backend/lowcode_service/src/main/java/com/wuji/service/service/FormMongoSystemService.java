package com.wuji.service.service;

import com.wuji.service.model.request.FormMongoSyncUserRequest;
import com.wuji.service.model.vo.FormMongoUserFieldVO;

import java.util.List;

public interface FormMongoSystemService {

    List<FormMongoUserFieldVO> getUserField();

    void syncUser(FormMongoSyncUserRequest formMongoSyncUserRequest);

}
