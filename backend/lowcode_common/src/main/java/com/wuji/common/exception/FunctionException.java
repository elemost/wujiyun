package com.wuji.common.exception;

import com.wuji.common.enums.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionException extends RuntimeException {

    private String code;
    private Integer subCode;
    private Object data;

    public FunctionException(ResultCode errorCode, String message) {
        super(String.format(errorCode.getMessage(), message));
        this.code = errorCode.getCode();
        this.subCode = errorCode.getSubCode();
    }
}
