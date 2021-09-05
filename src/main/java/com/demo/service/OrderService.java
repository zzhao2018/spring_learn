package com.demo.service;

import com.spring.Autowired;
import com.spring.Componet;
import com.spring.Scope;

@Componet("orderService")
@Scope("prototype")
public class OrderService {
    @Autowired
    private UserService userService;


    public void test() {
        System.out.println("userService:" + userService);
        userService.test();
    }
}
