package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.entity.SmsTemplateEntity;
import com.wuji.admin.model.request.SmsSendRequest;
import com.wuji.admin.model.vo.SendSmsVO;
import com.wuji.admin.service.AdminMessageService;
import com.wuji.admin.service.SmsTemplateService;
import com.wuji.admin.service.VerificationCodeService;
import com.wuji.admin.singleton.AliyunSmsClient;
import com.wuji.admin.singleton.TencentSmsClient;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@DS("slave")
public class AdminMessageServiceImpl implements AdminMessageService {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Override
    public void sendMessage(String mobile) {
        checkMessage(mobile);
        String code = StringUtil.getRandNum();
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("code", code);
        params.put("time", "5");
        sendSms(new SmsSendRequest.Builder().setSmsScene("短信验证码").setMobileList(Lists.newArrayList(mobile))
                .setParams(params).build());

        final long createTime = System.currentTimeMillis();
        // 将验证码放到redis
        verificationCodeService.insert(mobile, code, createTime);
        saveInRedis(mobile, code, createTime);
    }

    @Override
    public void sendSms(SmsSendRequest smsSendRequest) {
        // 获取短信模板
        SmsTemplateEntity template = smsTemplateService.getBySmsScene(smsSendRequest.getSmsScene());
        if (Objects.isNull(template)) {
            throw new AdminException(AdminResultCode.MESSAGE_TEMPLATE_NOT_EXIST, smsSendRequest.getSmsScene());
        }

        // 不同短信渠道
        switch (template.getProvider()) {
            case "alibaba":
                sendSmsByAli(smsSendRequest, template);
                break;
            case "tencent":
                sendSmsByTencent(smsSendRequest, template);
                break;
            default:
                throw new BizException(ResultCode.FAILED);
        }
    }

    private void sendSmsByAli(SmsSendRequest smsSendVo, SmsTemplateEntity template) {
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest =
                new com.aliyun.dysmsapi20170525.models.SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(String.join(",", smsSendVo.getMobileList())).setSignName(template.getSignName())
                .setTemplateCode(template.getTemplateId()).setTemplateParam(JSON.toJSONString(smsSendVo.getParams()));

        try {
            com.aliyun.dysmsapi20170525.models.SendSmsResponse sendSmsResponse =
                    AliyunSmsClient.getInstance().sendSms(sendSmsRequest);
            log.info("alibaba sms request:{},response:{}", JSON.toJSONString(sendSmsRequest),
                    JSON.toJSONString(sendSmsResponse));
        } catch (Exception ex) {
            log.error(Throwables.getStackTraceAsString(ex));
        }
    }

    private void sendSmsByTencent(SmsSendRequest smsSendVo, SmsTemplateEntity template) {
        SendSmsRequest req = new SendSmsRequest();
        req.setPhoneNumberSet(smsSendVo.getMobileList().stream().map(mobile -> "+86" + mobile).toArray(String[]::new));
        req.setTemplateParamSet(smsSendVo.getParams().values().stream().map(Objects::toString).toArray(String[]::new));
        req.setTemplateID(template.getTemplateId());
        req.setSmsSdkAppid(template.getAppId());
        req.setSign(template.getSignName());
        try {
            SendSmsResponse resp = TencentSmsClient.getInstance().SendSms(req);
            log.info("tencent sms request:{},response:{}", JSON.toJSONString(req), JSON.toJSONString(resp));
        } catch (TencentCloudSDKException ex) {
            log.error(Throwables.getStackTraceAsString(ex));
        }

    }

    private void saveInRedis(String mobile, String code, long createTime) {
        SendSmsVO sendSmsVO = new SendSmsVO();
        sendSmsVO.setCode(code);
        sendSmsVO.setCreateTime(createTime);
        Integer timeOut = 5 * 60;
        redisCache.setCacheObject("SMS_CODE_KEY:" + mobile + "_" + code,
                JSONObject.parse(JSONObject.toJSONString(sendSmsVO)), timeOut, TimeUnit.SECONDS);
    }

    private void checkMessage(String mobile) {
        boolean lock = redisCache.lock("phone_lock_key" + mobile, mobile, 2);
        if (!lock) {
            throw new AdminException(AdminResultCode.SEND_MESSAGE_REPEAT);
        }
        // SendSmsVO sendSmsVO = redisCache.getCacheObject(mobile);
        // if (sendSmsVO != null) {
        //     Long checkCreateTime = sendSmsVO.getCreateTime();
        //     if (checkCreateTime > System.currentTimeMillis() + 60 * 1000) {
        //         throw new AdminException(AdminResultCode.SEND_MESSAGE_LIMIT);
        //     }
        // }
    }
}
