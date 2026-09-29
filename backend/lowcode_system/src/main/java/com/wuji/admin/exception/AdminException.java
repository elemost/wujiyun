package com.wuji.admin.exception;

import com.wuji.admin.enums.AdminResultCode;
import com.wuji.common.exception.BizException;

public class AdminException extends BizException {

    public AdminException(AdminResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }

    public AdminException(AdminResultCode errorCode, Object... args) {
        super(errorCode.getCode(), errorCode.getSubCode(), String.format(errorCode.getMessage(), args));
    }
}
