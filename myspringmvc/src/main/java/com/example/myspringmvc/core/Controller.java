package com.example.myspringmvc.core;

import java.lang.annotation.*;

/**
 * @author tong
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
