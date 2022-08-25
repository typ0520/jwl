package com.example.myspringmvc;

import com.example.myspringmvc.core.RouterManager;
import org.junit.Test;

/**
 * @author tong
 */
public class RouterManagerTest {
    @Test
    public void test_init() {
        new RouterManager().init("com.example.myspringmvc.example");
    }
}
