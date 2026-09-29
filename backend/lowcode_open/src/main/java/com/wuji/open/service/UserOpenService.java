package com.wuji.open.service;

import com.wuji.open.model.request.UserDeleteOpenRequest;
import com.wuji.open.model.request.UserInfoRequest;
import com.wuji.open.model.request.UserOpenSaveRequest;
import com.wuji.open.model.request.UserOpenUpdateRequest;
import com.wuji.open.model.vo.UserOpenVO;

public interface UserOpenService {
    void create(UserOpenSaveRequest userOpenSaveRequest);

    void update(UserOpenUpdateRequest userOpenUpdateRequest);

    UserOpenVO queryByPhone(UserInfoRequest userInfoRequest);

    void delete(UserDeleteOpenRequest userInfoRequest);
}
