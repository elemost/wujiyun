package com.wuji.common.model;


import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import lombok.Data;

/**
 * 通用返回对象
 *
 * @param <T>
 * @author Jackie
 * @date 2022-11-09
 */
@Data
public class Response<T> {
    private String code;
    private Integer subCode;
    private String message;
    private T data;
    private Object otherData;

    protected Response() {
    }

    protected Response(String code, Integer subCode, String message, T data) {
        this.code = code;
        this.subCode = subCode;
        this.message = message;
        this.data = data;
    }

    public static Response success() {
        return success(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取数据
     * @param <T>
     * @return
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getSubCode(),
                ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @param <T>
     * @return
     */
    public static <T> Response<T> failed(ResultCode errorCode, String message) {
        return new Response<>(errorCode.getCode(), ResultCode.FAILED.getSubCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @param <T>
     * @return
     */
    public static <T> Response<T> failed(String message) {
        return new Response<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getSubCode(), message, null);
    }

    public static <T> Response<T> failed(String message, T data) {
        return new Response<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getSubCode(), message, data);
    }

    public static <T> Response<T> failed(BizException bizException) {
        return new Response<>(bizException.getCode(), bizException.getSubCode(), bizException.getMessage(), null);
    }


    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @param <T>
     * @return
     */
    public static <T> Response<T> failed(String message, String code) {
        return new Response<>(code, null, message, null);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 错误信息
     * @param <T>
     * @return
     */
    public static <T> Response<T> validateFailed(String message) {
        return new Response<>(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getSubCode(), message,
                null);
    }
}
