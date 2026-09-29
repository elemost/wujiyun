package com.wuji.common.privilege.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰资源唯一标识符字段，进行资源权限校验时会从被该注解修饰的字段中拿取资源唯一标识的值，比如uuid的值
 *
 * @since 2021-10-14
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationId {
}

