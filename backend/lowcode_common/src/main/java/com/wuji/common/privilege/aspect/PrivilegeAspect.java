package com.wuji.common.privilege.aspect;

import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.privilege.annotation.ApplicationId;
import com.wuji.common.privilege.annotation.Resource;
import com.wuji.common.privilege.annotation.ResourceId;
import com.wuji.common.privilege.annotation.Secure;
import com.wuji.common.privilege.resource.ResourceValidator;
import com.wuji.common.privilege.utils.AspectUtils;
import com.wuji.common.utils.ToolSpring;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 资源权限校验
 */
@Aspect
@Component
public class PrivilegeAspect {

    @Pointcut(
            "execution(public * com.wuji..controller..*.*(..)) && @annotation(com.wuji.common.privilege.annotation.Secure)")
    public void controllerMethod() {
        // 切入前不需要做什么
    }

    @Before("controllerMethod()") //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {
        HttpServletRequest request = getServletRequest();
        if (request == null) {
            return;
        }
        Secure secure = getSecure(joinPoint);
        for (Resource resource : secure.value()) {

            // 校验资源权限，即对于不同类型资源，做针对性的权限判断
            checkResourcePermission(joinPoint, resource);
        }
    }

    private void checkResourcePermission(JoinPoint joinPoint, Resource resource) {
        ResourceValidator resourceValidator = getResourceValidator(resource.resourceValidator());
        List<String> resourceIdentifierValues =
                AspectUtils.getResourceIdentifierValues(joinPoint, resource.identifierLocation(), ResourceId.class);
        List<String> applicationIdentifierValues =
                AspectUtils.getResourceIdentifierValues(joinPoint, resource.applicationLocation(), ApplicationId.class);
        String applicationId = "";
        if (CollectionUtils.isNotEmpty(applicationIdentifierValues)) {
            applicationId = applicationIdentifierValues.get(0);
        }
        if (!resourceValidator.validate(resourceIdentifierValues, applicationId)) {
            throw new BizException(ResultCode.NO_AUTH_1);
        }
    }

    private ResourceValidator getResourceValidator(Class<? extends ResourceValidator> resourceValidatorClass) {
        return ToolSpring.getBean(resourceValidatorClass);
    }

    private HttpServletRequest getServletRequest() {
        RequestAttributes requestAttributes =
                RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if (requestAttributes == null) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    private Secure getSecure(JoinPoint joinPoint) {
        Method method = AspectUtils.getMethod(joinPoint);
        return method.getAnnotation(Secure.class);
    }
}



