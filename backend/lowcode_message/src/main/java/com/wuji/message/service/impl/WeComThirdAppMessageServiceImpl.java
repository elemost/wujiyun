package com.wuji.message.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.CompanyPullConfigCache;
import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.admin.client.wecom.model.AppMessageRequest;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.CompanyPullConfigService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.model.info.WeComConfig;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.message.enums.SendMessagePlatformEnum;
import com.wuji.message.enums.WeComAppMessageTypeEnum;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("weComThirdAppMessageServiceImpl")
@Slf4j
public class WeComThirdAppMessageServiceImpl implements MessageSendService {

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private CompanyPullConfigService companyPullConfigService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private WeComThirdService weComThirdServiceImpl;

    @Override
    public String sendPlatform() {
        return SendMessagePlatformEnum.WECOM_THIRD.name();
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
        String accessToken = weComThirdServiceImpl.getAccessTokenBySuitId(sendMessageRequest.getCompanyId(),
                sendMessageRequest.getSuiteId());
        AppMessageRequest appMessageRequest = new AppMessageRequest();
        CompanyPullConfigVO companyPullConfigVO =
                CompanyPullConfigCache.getValue(sendMessageRequest.getCompanyId(), sendMessageRequest.getSuiteId());
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        appMessageRequest.setAgentid(weComConfig.getClientId());
        appMessageRequest.setTouser(StringUtils.join(thirdId, "|"));
        appMessageRequest.setMsgtype(sendMessageRequest.getMessageType());
        if (WeComAppMessageTypeEnum.TEXT.getType().equalsIgnoreCase(sendMessageRequest.getMessageType())) {
            Map<String, String> content = new HashMap<>();
            content.put("content", sendMessageRequest.getMessage());
            appMessageRequest.setText(content);
        } else if (WeComAppMessageTypeEnum.MARKDOWN.getType().equals(sendMessageRequest.getMessageType())) {
            Map<String, String> content = new HashMap<>();
            content.put("content", sendMessageRequest.getMessage());
            appMessageRequest.setMarkdown(content);
        }
        JSONObject jsonObject = weComClient.sendAppMessage(accessToken, appMessageRequest);
        log.info("jsonobject" + jsonObject.toJSONString());
    }

}
