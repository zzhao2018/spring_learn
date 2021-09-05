package com.demo.service;

import com.spring.Autowired;
import com.spring.Componet;
import com.spring.Scope;

@Componet("userService")
public class UserService {
    @Autowired
    private AddrService addrService;

    public void test() {
        addrService.test();
        System.out.println("addrService:" + addrService);
    }

}
