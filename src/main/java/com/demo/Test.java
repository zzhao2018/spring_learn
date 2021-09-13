package com.demo;

import com.demo.service.OrderService;
import com.demo.service.OrderServiceImpl;
import com.spring.ZApplicationContext;

public class Test {
    public static void main(String[] args) throws Exception {
        ZApplicationContext applicationContext = new ZApplicationContext(AppConfig.class);
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");

        orderService.test();
//        System.out.println("orderService:" + orderService);

//        System.out.println("================");
//        System.out.println("addrService:" + applicationContext.getBean("addrService"));
//        System.out.println("orderService:" + applicationContext.getBean("orderService"));
//        System.out.println("cityService:" + applicationContext.getBean("cityService"));
//        System.out.println("cityService:" + applicationContext.getBean("cityService"));
//        System.out.println("UserService:"+applicationContext.getBean("userService"));
    }
}
