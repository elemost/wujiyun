package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.info.UserScope;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.common.service.MessageCommonService;
import com.wuji.common.utils.UserUtils;
import com.wuji.plugin.model.info.config.InMailPluginConfig;
import com.wuji.plugin.service.PluginUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMailPluginImpl implements PluginUseService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageCommonService messageService;

    @Override
    public String pluginType() {
        return "IN_MAIL";
    }

    @Override
    public Object execute(Object object) {
        InMailPluginConfig inMailPlugin =
                JSONObject.parseObject(JSONObject.toJSONString(object), InMailPluginConfig.class);
        List<UserScope> scopes = inMailPlugin.getScopes();
        List<Long> userIdList = userService.getUserByScope(scopes);
        MessageInsertRequest messageInsertRequest = new MessageInsertRequest();
        messageInsertRequest.setUserIdList(userIdList);
        messageInsertRequest.setContent(inMailPlugin.getContent());
        messageInsertRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        messageInsertRequest.setSource(inMailPlugin.getSource());
        messageInsertRequest.setMessageType(inMailPlugin.getMessageType());
        messageService.insert(messageInsertRequest);
        return null;
    }
}
