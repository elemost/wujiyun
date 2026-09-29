package com.wuji.message.service.impl;

import com.aliyun.dingtalkrobot_1_0.Client;
import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOHeaders;
import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTORequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.wuji.admin.cache.DingTalkCache;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.exception.BizException;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.enums.SendMessagePlatformEnum;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("dingTalkMessageServiceImpl")
@Slf4j
public class DingTalkMessageServiceImpl implements MessageSendService {

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String sendPlatform() {
        return SendMessagePlatformEnum.DING_TALK.name();
    }

    @Override
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(sendMessageRequest.getUserIdList());
        List<String> thirdId =
                userCompanyVOS.stream().map(UserCompanyVO::getDingThirdId).filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdId)) {
            return;
        }
        Client client = createClient();
        BatchSendOTOHeaders batchSendOTOHeaders = new BatchSendOTOHeaders();
        batchSendOTOHeaders.xAcsDingtalkAccessToken = DingTalkCache.getAccessToken(UserUtils.getUser().getCompanyId());
        BatchSendOTORequest batchSendOTORequest = new BatchSendOTORequest().setMsgParam(sendMessageRequest.getMessage())
                .setMsgKey(sendMessageRequest.getMessageType()).setRobotCode(sendMessageRequest.getRobotId())
                .setUserIds(thirdId);
        try {
            client.batchSendOTOWithOptions(batchSendOTORequest, batchSendOTOHeaders, new RuntimeOptions());
        } catch (Exception e) {
            log.error("发送短信失败", e);
            throw new BizException(e.getMessage());
        }
    }

    private static Client createClient() {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            return new Client(config);
        } catch (Exception e) {
            log.error("获取钉钉client失败", e);
            throw new AdminException(AdminResultCode.DING_TALK_CLIENT_ERROR);
        }
    }
}
