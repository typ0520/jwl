package com.example.myspringmvc.core;

import java.lang.annotation.*;

/**
 * @author tong
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {
    String[] value() default {};
}