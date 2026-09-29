package com.wuji.console.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.Response;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public Response<String> handle(HttpServletRequest request, BizException e) {
        log.error("业务异常，{}，错误信息：{}", getReqInfo(request), e.getMessage(), e);
        if (e.getMessage() != null) {
            Response<String> response = Response.failed(e);
            response.setOtherData(e.getData());
            return response;
        }
        return Response.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = ForbiddenException.class)
    public void handle(HttpServletRequest request, ForbiddenException e, HttpServletResponse response)
            throws IOException {
        log.error("没有权限，{}，错误信息：{}", getReqInfo(request), e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String s = JSON.toJSONString(Response.failed(e.getMessage()));
        ServletOutputStream os = response.getOutputStream();
        IOUtils.copy(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)), os);
        os.flush();
    }

    @ResponseBody
    @ExceptionHandler(value = FeignException.class)
    public Response<String> handle(HttpServletRequest request, FeignException e) {
        log.error("feign", e);
        return Response.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response<String> handleValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error("validate parameters error:{}", getReqInfo(request), e);
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        return Response.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Response<String> handleValidException(HttpServletRequest request, BindException e) {
        log.error("request error BindException:{}", getReqInfo(request), e);
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return Response.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Response<String> handleJsonException(HttpServletRequest request, JSONException e) {
        log.error("request error JSONException:{}", getReqInfo(request), e);
        return Response.validateFailed("");
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<String> handleOtherException(HttpServletRequest request, Exception e) {
        log.error("request error Exception:{}", getReqInfo(request), e);
        return Response.validateFailed("哎呀，系统临时打了个小盹～");
    }

    private String getReqInfo(HttpServletRequest request) {
        return String.format("method:%s,uri:%s", request.getMethod(), request.getRequestURI());
    }
}
