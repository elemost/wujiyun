package com.wuji.common.privilege.utils;

import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AspectUtils {

    public static List<String> getResourceIdentifierValues(JoinPoint joinPoint, IdentifierLocationEnum type,
                                                           Class<? extends Annotation> annotationClass) {
        Object argValue = null;
        if (type == IdentifierLocationEnum.REQ_PRAM) {
            argValue = getArgValue(joinPoint, false, annotationClass);
        } else if (type == IdentifierLocationEnum.PATH_VAR) {
            argValue = getArgValue(joinPoint, true, annotationClass);
        } else if (type == IdentifierLocationEnum.BODY_FIELD) {
            Object bodyObj = Objects.requireNonNull(getRequestBody(joinPoint));
            Field field = null;
            field = Arrays.stream(bodyObj.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(annotationClass)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("resource id in request body not found"));
            field.setAccessible(true);
            try {
                argValue = field.get(bodyObj);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            return null;
        }
        if (argValue == null) {
            return null;
        }
        return convertToStringList(argValue);
    }

    private static Object getRequestBody(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        Parameter[] parameters = method.getParameters();
        int i;
        for (i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                break;
            }
        }
        if (i == parameters.length) {
            return null;
        }
        return getArgValues(joinPoint)[i];
    }

    public static Object[] getArgValues(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    public static Object getArgValue(JoinPoint joinPoint, int index) {
        return joinPoint.getArgs()[index];
    }

    public static Object getArgValue(JoinPoint joinPoint, boolean isPathVariable, Class<? extends Annotation> annotationClass) {
        Method method = getMethod(joinPoint);
        Parameter[] parameters = method.getParameters();
        int pathVariableIndex;
        if ((pathVariableIndex = isPathVariable ? searchForPathVariableIndex(parameters, annotationClass) :
                searchForRequestParamIndex(parameters, annotationClass)) == -1) {
            return null;
        }
        return getArgValue(joinPoint, pathVariableIndex);
    }

    private static int searchForPathVariableIndex(Parameter[] parameters, Class tClass) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class) && parameters[i].isAnnotationPresent(tClass)) {
                return i;
            }
        }
        return -1;
    }

    private static int searchForRequestParamIndex(Parameter[] parameters, Class tClass) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(tClass)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static List<String> convertToStringList(Object argValue) {
        if (argValue instanceof String || argValue instanceof Long || argValue instanceof Integer) {
            return Collections.singletonList(String.valueOf(argValue));
        } else if (argValue instanceof List) {
            return (List<String>) argValue;
        }
        return null;
    }

    public static Method getMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getMethod();
    }
}
