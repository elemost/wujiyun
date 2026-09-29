package com.wuji.admin.aspect.corp.aspect;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.admin.components.CorpCoopComponent;
import com.wuji.common.privilege.utils.AspectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 资源权限校验
 */
@Aspect
@Component
public class CorpCoopAspect {

    @Autowired
    private CorpCoopComponent corpCoopComponent;

    @Pointcut(
            "execution(public * com.wuji..controller..*.*(..)) && @annotation(com.wuji.admin.aspect.corp.annotation.CorpCoopResource)")
    public void controllerMethod() {
        // 切入前不需要做什么
    }

    @Before("controllerMethod()") //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {
        CorpCoopResource corpCoop = getCorpCoop(joinPoint);
        List<String> resourceIdentifierValues =
                AspectUtils.getResourceIdentifierValues(joinPoint, corpCoop.location(), CorpCoop.class);
        if (CollectionUtils.isNotEmpty(resourceIdentifierValues)) {
            String companyUuid = resourceIdentifierValues.get(0);
            corpCoopComponent.exchangeCompany(companyUuid);
        }

    }

    private CorpCoopResource getCorpCoop(JoinPoint joinPoint) {
        Method method = AspectUtils.getMethod(joinPoint);
        return method.getAnnotation(CorpCoopResource.class);
    }
}



