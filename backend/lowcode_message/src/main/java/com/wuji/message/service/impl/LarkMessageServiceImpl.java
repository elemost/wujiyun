package com.wuji.message.service.impl;

import com.wuji.admin.client.lark.LarkClient;
import com.wuji.admin.client.lark.model.LarkSendMessageRequest;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.message.enums.SendMessagePlatformEnum;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("larkMessageServiceImpl")
public class LarkMessageServiceImpl implements MessageSendService {
    @Autowired
    private LarkClient larkClient;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String sendPlatform() {
        return SendMessagePlatformEnum.LARK.name();
    }

    @Override
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(sendMessageRequest.getUserIdList());
        List<String> thirdIdList =
                userCompanyVOS.stream().map(UserCompanyVO::getLarkUserId).filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdIdList)) {
            return;
        }
        for (String third : thirdIdList) {
            LarkSendMessageRequest larkSendMessageRequest = new LarkSendMessageRequest();
            larkSendMessageRequest.setReceive_id(third);
            larkSendMessageRequest.setContent(sendMessageRequest.getMessage());
            larkSendMessageRequest.setMsg_type(sendMessageRequest.getMessageType());
            larkClient.sendMessage(sendMessageRequest.getReceiveType(), larkSendMessageRequest);
        }

    }
}
