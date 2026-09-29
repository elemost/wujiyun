package com.wuji.service.utils;

import com.wuji.service.cache.ApplicationCache;
import com.wuji.service.enums.ApplicationNatureEnum;
import com.wuji.service.model.vo.ApplicationVO;

import java.util.Date;

public class FunctionUtils {
    public static Boolean checkApplicationFunction(String applicationId) {
        ApplicationVO detail = ApplicationCache.getDetail(applicationId);
        if (detail == null) {
            return false;
        }
        if (!ApplicationNatureEnum.NORMAL.name().equals(detail.getApplicationNature())) {
            return detail.getExpireTime() > new Date().getTime();
        }
        return null;
    }
}
