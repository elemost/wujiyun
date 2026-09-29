package com.wuji.message.service.impl;

import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.common.service.MessageCommonService;
import com.wuji.message.enums.SendMessagePlatformEnum;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InMailServiceImpl implements MessageSendService {

    @Autowired
    private MessageCommonService messageService;

    @Override
    public String sendPlatform() {
        return SendMessagePlatformEnum.IN_MAIL.name();
    }

    @Override
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        MessageInsertRequest messageInsertRequest = new MessageInsertRequest();
        messageInsertRequest.setMessageType(sendMessageRequest.getMessageType());
        messageInsertRequest.setSource(sendMessageRequest.getSource());
        messageInsertRequest.setContent(sendMessageRequest.getMessage());
        messageInsertRequest.setCompanyId(sendMessageRequest.getCompanyId());
        messageInsertRequest.setUserIdList(sendMessageRequest.getUserIdList());
        messageService.insert(messageInsertRequest);
    }
}
