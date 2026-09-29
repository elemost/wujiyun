package com.wuji.common.privilege.annotation;


import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.common.privilege.resource.ResourceValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源授权规则注解，根据类别的不同有不同的定义
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    /**
     * 资源标识符在请求中的位置<br/>
     * REQ_PRAM 在请求参数中<br/>
     * JSON_PATH 在请求body中<br/>
     * PATH_VAR 在请求url的path中<br/>
     * NONE 无需资源标识符
     */
    IdentifierLocationEnum identifierLocation() default IdentifierLocationEnum.NONE;

    IdentifierLocationEnum applicationLocation() default IdentifierLocationEnum.NONE;

    /**
     * 自定义检查class
     * @return
     */
    Class<?extends ResourceValidator> resourceValidator();
}

