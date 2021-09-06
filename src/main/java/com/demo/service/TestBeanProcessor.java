package com.demo.service;

import com.demo.service.UserService;
import com.spring.BeanPostProcessor;
import com.spring.Componet;

@Componet("testBeanProcessor")
public class TestBeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("初始化前");
            UserService service = (UserService) bean;
            service.setName("auto inject name");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("初始化后");
        }
        return bean;
    }
}
