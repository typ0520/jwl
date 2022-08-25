package com.example.myspringmvc;

import com.example.myspringmvc.core.DispatcherServlet;
import org.junit.Test;

/**
 * @author tong
 */
public class DispatcherServletTest {
    @Test
    public void test_init() {
        new DispatcherServlet().init("com.example.myspringmvc.example");
    }
}
