package com.example.myspringmvc;

import com.example.myspringmvc.core.FrameworkServlet;
import org.junit.Test;

/**
 * @author tong
 */
public class FrameworkServletTest {
    @Test
    public void test_init() {
        new FrameworkServlet().init("com.example.myspringmvc.example");
    }
}
