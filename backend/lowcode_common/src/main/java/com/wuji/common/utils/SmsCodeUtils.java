package com.wuji.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.utils.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsCodeUtils {

    @Autowired
    private RedisCache redisCache;

    public void checkCode(String phone, String code) {

        if (code == null || code.isEmpty()) {
            throw new BizException(ResultCode.MESSAGE_CODE_ERROR);
        }
        String key = "SMS_CODE_KEY:" + phone + "_" + code;
        if (Boolean.FALSE.equals(redisCache.hasKey(key))) {
            throw new BizException(ResultCode.MESSAGE_CODE_ERROR);
        }
        JSONObject sendSmsVO = redisCache.getCacheObject(key);

        if (sendSmsVO == null) {
            throw new BizException(ResultCode.MESSAGE_CODE_ERROR);
        }
        redisCache.deleteObject(key);
    }
}
