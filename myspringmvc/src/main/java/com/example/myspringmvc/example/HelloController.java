package com.example.myspringmvc.example;

import com.example.myspringmvc.core.GetMapping;
import com.example.myspringmvc.core.RequestParam;
import com.example.myspringmvc.core.ResponseBody;

/**
 * @author tong
 */
public class HelloController {
    @GetMapping
    @ResponseBody
    public String hello(@RequestParam(value = "name", defaultValue = "world") String name) {
        return "hello: " + name;
    }
}
