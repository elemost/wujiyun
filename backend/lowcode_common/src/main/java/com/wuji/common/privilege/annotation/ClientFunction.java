package com.wuji.common.privilege.annotation;

import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.common.privilege.resource.AbstractFunctionValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientFunction {
    String resourceCode();

    String resourceName() default "";

    /**
     * 自定义检查class
     *
     * @return
     */
    Class<? extends AbstractFunctionValidator> resourceValidator();

    IdentifierLocationEnum applicationLocation() default IdentifierLocationEnum.NONE;
}
