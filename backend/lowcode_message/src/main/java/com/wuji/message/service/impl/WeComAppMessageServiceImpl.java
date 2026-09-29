package com.wuji.message.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.WeComCache;
import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.admin.client.wecom.model.AppMessageRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.model.info.WeComConfig;
import com.wuji.message.enums.SendMessagePlatformEnum;
import com.wuji.message.enums.WeComAppMessageTypeEnum;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("weComAppMessageServiceImpl")
public class WeComAppMessageServiceImpl implements MessageSendService {

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String sendPlatform() {
        return SendMessagePlatformEnum.WECOM.name();
    }

    @Override
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(sendMessageRequest.getUserIdList());
        List<String> thirdId =
                userCompanyVOS.stream().map(UserCompanyVO::getWeComUserId).filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdId)) {
            return;
        }
        String accessToken = WeComCache.getAccessToken(sendMessageRequest.getCompanyId());
        CompanyVO info = companyService.info(sendMessageRequest.getCompanyId());
        AppMessageRequest appMessageRequest = new AppMessageRequest();
        WeComConfig weComConfig = JSONObject.parseObject(info.getPullConfig(), WeComConfig.class);
        appMessageRequest.setAgentid(weComConfig.getClientId());
        appMessageRequest.setTouser(StringUtils.join(thirdId, "|"));
        appMessageRequest.setMsgtype(sendMessageRequest.getMessageType());
        if (WeComAppMessageTypeEnum.TEXT.getType().equals(sendMessageRequest.getMessageType())) {
            Map<String, String> content = new HashMap<>();
            content.put("content", sendMessageRequest.getMessage());
            appMessageRequest.setText(content);
        } else if  (WeComAppMessageTypeEnum.MARKDOWN.getType().equals(sendMessageRequest.getMessageType())) {
            Map<String, String> content = new HashMap<>();
            content.put("content", sendMessageRequest.getMessage());
            appMessageRequest.setMarkdown(content);
        }
         weComClient.sendAppMessage(accessToken, appMessageRequest);
    }
}
