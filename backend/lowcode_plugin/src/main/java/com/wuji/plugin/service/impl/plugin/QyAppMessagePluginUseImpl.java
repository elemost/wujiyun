package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.message.service.MessageSendService;
import com.wuji.plugin.model.request.QyAppMessageRequest;
import com.wuji.plugin.service.PluginUseService;
import com.wuji.plugin.utils.FormMarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QyAppMessagePluginUseImpl implements PluginUseService {

    @Autowired
    private MessageSendService weComAppMessageServiceImpl;

    @Override
    public String pluginType() {
        return "QIYE_MESSAGE";
    }

    @Override
    public Object execute(Object object) {
        QyAppMessageRequest qyAppMessageRequest =
                JSONObject.parseObject(JSONObject.toJSONString(object), QyAppMessageRequest.class);
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setUserIdList(qyAppMessageRequest.getUserIdList());
        sendMessageRequest.setMessageType("markdown");
        sendMessageRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        sendMessageRequest.setMessage(FormMarkdownUtils.markContent(qyAppMessageRequest.getConfigList()));
        weComAppMessageServiceImpl.sendMessage(sendMessageRequest);
        return null;
    }
}
