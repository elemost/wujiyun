package com.wuji.common.privilege.aspect;

import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.privilege.annotation.ApplicationId;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.common.privilege.resource.AbstractFunctionValidator;
import com.wuji.common.privilege.utils.AspectUtils;
import com.wuji.common.utils.ToolSpring;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class ClientFunctionAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut(
            "execution(public * com.wuji..controller..*.*(..)) && @annotation(com.wuji.common.privilege.annotation.ClientFunction)")
    public void controllerMethod() {
        // 切入前不需要做什么
    }

    @Before("controllerMethod()") //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {
        ClientFunction clientFunction = getClientFunction(joinPoint);
        boolean validate;
        AbstractFunctionValidator abstractFunctionValidator = getResourceValidator(clientFunction.resourceValidator());
        if (clientFunction.applicationLocation() == IdentifierLocationEnum.NONE) {
            validate = abstractFunctionValidator.validate(null, clientFunction.resourceCode(),
                    clientFunction.resourceName());
        } else {
            List<String> applicationIdList =
                    AspectUtils.getResourceIdentifierValues(joinPoint, clientFunction.applicationLocation(),
                            ApplicationId.class);
            String applicationId = null;
            if (CollectionUtils.isNotEmpty(applicationIdList)) {
                applicationId = applicationIdList.get(0);
            }
            validate = abstractFunctionValidator.validate(applicationId, clientFunction.resourceCode(),
                    clientFunction.resourceName());
        }
        if (!validate) {
            throw new BizException(ResultCode.NO_AUTH_APP);
        }
    }

    private AbstractFunctionValidator getResourceValidator(
            Class<? extends AbstractFunctionValidator> resourceValidatorClass) {
        return ToolSpring.getBean(resourceValidatorClass);
    }

    private ClientFunction getClientFunction(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        return method.getAnnotation(ClientFunction.class);
    }

    private Method getMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getMethod();
    }
}
