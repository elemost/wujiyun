package com.wuji.admin.service;

import com.wuji.admin.model.request.SmsSendRequest;

public interface AdminMessageService {
    void sendMessage(String mobile);

    void sendSms(SmsSendRequest smsSendRequest);
}
