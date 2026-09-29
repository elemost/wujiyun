package com.wuji.common.exception;


import com.wuji.common.enums.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义API异常
 *
 * @author Jackie
 * @date 2022-11-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {
    private String code;
    private Integer subCode;
    private Object data;

    public BizException(ResultCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.subCode = errorCode.getSubCode();
    }

    public BizException(ResultCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.subCode = errorCode.getSubCode();
    }

    public BizException(ResultCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.subCode = errorCode.getSubCode();
    }

    public BizException(String code, Integer subCode, String message) {
        super(message);
        this.code = code;
        this.subCode = subCode;
    }

    public BizException(String code, Integer subCode, String message, Object data) {
        super(message);
        this.code = code;
        this.subCode = subCode;
        this.data = data;
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(ResultCode errorCode, Object... message) {
        super(String.format(errorCode.getMessage(), message));
        this.code = errorCode.getCode();
        this.subCode = errorCode.getSubCode();
    }
}
