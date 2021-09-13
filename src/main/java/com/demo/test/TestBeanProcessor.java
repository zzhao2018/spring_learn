package com.demo.test;

import com.spring.InitBean.BeanPostProcessor;
import com.spring.ioc.Componet;

@Componet("testBeanProcessor")
public class TestBeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
//        if (beanName.equals("userService")) {
//            System.out.println("初始化前");
//            UserService service = (UserService) bean;
//            service.setName("auto inject name");
//        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
//        if (beanName.equals("userService")) {
//            System.out.println("初始化后");
//        }
        return bean;
    }
}
