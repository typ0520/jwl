package com.example.jwl;

/**
 * @author tong
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Table {
    java.lang.String name() default "";
}
