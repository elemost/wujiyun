package com.wuji.framework.exception;

import com.wuji.common.exception.BizException;
import com.wuji.framework.enums.FrameworkResultCode;

public class FrameworkException extends BizException {
    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(FrameworkResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }
}
