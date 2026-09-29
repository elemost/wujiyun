package com.wuji.message.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.service.UserService;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.vo.ConfigVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.service.ConfigService;
import com.wuji.message.model.info.EmailConfig;
import com.wuji.message.service.EmailService;
import com.wuji.message.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @Override
    public void sendEmailByEmail(List<String> emailList, String html, String title) {
        ConfigVO configVO = configService.detailByKey(ConfigEnum.LOWCODE_EMAIL_CONFIG.name());
        String configValue = configVO.getConfigValue();
        EmailConfig emailConfig = JSONObject.parseObject(configValue, EmailConfig.class);
        EmailUtil.sendEmail(emailList, html, title, emailConfig);
    }

    @Override
    public void sendEmail(List<String> userIdList, String html, String title) {
        List<Long> userId = userIdList.stream().map(Long::valueOf).collect(Collectors.toList());
        List<UserVO> userVOS = userService.queryByIds(userId);
        List<String> emailList = userVOS.stream().map(UserVO::getEmail).collect(Collectors.toList());
        ConfigVO configVO = configService.detailByKey(ConfigEnum.LOWCODE_EMAIL_CONFIG.name());
        String configValue = configVO.getConfigValue();
        EmailConfig emailConfig = JSONObject.parseObject(configValue, EmailConfig.class);
        EmailUtil.sendEmail(emailList, html, title, emailConfig);
    }
}
