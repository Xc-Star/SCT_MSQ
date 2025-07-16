package com.sct.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: SuperPermission
 * @Author Xc_Star
 * @Package com.sct.annotation
 * @Date 2025/7/16 14:17
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SuperPermission {
}
