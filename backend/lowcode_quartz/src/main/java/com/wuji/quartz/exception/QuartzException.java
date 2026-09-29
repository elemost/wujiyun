package com.wuji.quartz.exception;

import com.wuji.common.exception.BizException;
import com.wuji.quartz.enums.QuartzResultCode;

public class QuartzException extends BizException {

    public QuartzException(QuartzResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }

    public QuartzException(QuartzResultCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getSubCode(), String.format(errorCode.getMessage(), args));
    }
}
