package com.wuji.service.exception;

import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.service.enums.ServiceResultCode;

public class ServiceException extends BizException {

    public ServiceException(ServiceResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }

    public ServiceException(ResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }

    public ServiceException(ServiceResultCode errorCode, String message) {
        super(errorCode.getCode(), errorCode.getSubCode(), String.format(errorCode.getMessage(), message));
    }

    public ServiceException(ServiceResultCode errorCode, Object data) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage(), data);
    }

    public ServiceException(ServiceResultCode errorCode, String message, Object data) {
        super(errorCode.getCode(), errorCode.getSubCode(), message, data);
    }

    public ServiceException(ServiceResultCode errorCode, Object... data) {
        super(errorCode.getCode(), errorCode.getSubCode(), String.format(errorCode.getMessage(), data));
    }


    public ServiceException(String message) {
        super(message);
    }
}
