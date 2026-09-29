package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.message.model.info.EmailConfig;
import com.wuji.message.utils.EmailUtil;
import com.wuji.plugin.model.info.config.EmailPluginConfig;
import com.wuji.plugin.service.PluginUseService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailPluginImpl implements PluginUseService {

    @Override
    public String pluginType() {
        return "EMAIL";
    }

    @Override
    public Object execute(Object object) {
        EmailPluginConfig emailPluginConfig =
                JSONObject.parseObject(JSONObject.toJSONString(object), EmailPluginConfig.class);
        List<String> emailList = Arrays.stream(emailPluginConfig.getSendUser().split(",")).collect(Collectors.toList());
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setPassword(emailPluginConfig.getPassword());
        emailConfig.setMailPort(emailPluginConfig.getMailPort());
        emailConfig.setUserName(emailPluginConfig.getUserName());
        emailConfig.setMailHost(emailPluginConfig.getMailHost());
        EmailUtil.sendEmail(emailList, emailPluginConfig.getContent(), emailPluginConfig.getTitle(), emailConfig);
        return null;
    }
}
