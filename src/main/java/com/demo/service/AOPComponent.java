package com.demo.service;

import com.spring.aop.After;
import com.spring.aop.Before;
import com.spring.ioc.Componet;
import com.spring.aop.Aspect;

@Componet
@Aspect
public class AOPComponent {

    @Before(value = "test")
    public void logStart() {
        System.out.println("start test.....");
    }

    @After(value = "test")
    public void logEnd() {
        System.out.println("end test....");
    }
}
