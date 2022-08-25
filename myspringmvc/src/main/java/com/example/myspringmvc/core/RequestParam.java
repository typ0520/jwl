package com.example.myspringmvc.core;

import java.lang.annotation.*;

/**
 * @author tong
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";

    boolean required() default true;

    String defaultValue() default "";
}
