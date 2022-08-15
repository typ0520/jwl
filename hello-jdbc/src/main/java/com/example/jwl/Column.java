package com.example.jwl;

import java.lang.annotation.ElementType;

/**
 * @author tong
 */
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
}
