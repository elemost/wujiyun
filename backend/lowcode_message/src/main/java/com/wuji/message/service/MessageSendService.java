package com.wuji.message.service;

import com.wuji.message.model.request.SendMessageRequest;

public interface MessageSendService {

    String sendPlatform();

    void sendMessage(SendMessageRequest sendMessageRequest);
}
