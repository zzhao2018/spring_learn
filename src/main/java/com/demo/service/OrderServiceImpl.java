package com.demo.service;

import com.spring.ioc.Componet;

@Componet("orderService")
//@Scope("prototype")
public class OrderServiceImpl implements OrderService {

    public void test() {
        System.out.println("orderService test");
    }
}
