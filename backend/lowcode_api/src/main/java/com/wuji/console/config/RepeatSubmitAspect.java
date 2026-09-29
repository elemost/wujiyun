package com.wuji.console.config;


import com.wuji.common.utils.UserUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class RepeatSubmitAspect {

    @AfterReturning("execution(* com.wuji.*.controller..*(..)) && !execution(* com.wuji.console..*(..))") // 匹配需要拦截的方法
    public void afterReturn() {
        // 在方法执行后清理 TransmittableThreadLocal
        UserUtils.clearUser();
    }

    @AfterThrowing("execution(* com.wuji..*(..)) && !execution(* com.wuji.console..*(..))") // 匹配需要拦截的方法
    public void afterMethod() {
        // 在方法执行后清理 TransmittableThreadLocal
        // UserUtils.clearUser();
    }
}
