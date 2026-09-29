package com.wuji.open.exception;

import com.wuji.common.exception.BizException;
import com.wuji.open.enums.OpenResultCode;

public class OpenException extends BizException {

    public OpenException(OpenResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }

    public OpenException(OpenResultCode errorCode, String message) {
        super(errorCode.getCode(), errorCode.getSubCode(), String.format(errorCode.getMessage(), message));
    }

    public OpenException(OpenResultCode errorCode, Object data) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage(), data);
    }
}
