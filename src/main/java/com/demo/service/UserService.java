package com.demo.service;

import com.spring.Autowired;
import com.spring.BeanNameAware;
import com.spring.Componet;
import com.spring.Scope;

@Componet("userService")
public class UserService implements BeanNameAware {
    @Autowired
    private AddrService addrService;

    private String beanName;


    public void test() {
        addrService.test();
        System.out.println("addrService:" + addrService);
        System.out.println("userService bean name:" + beanName);

    }

    @Override
    public void setBeanName(String var1) {
        this.beanName = var1;
    }
}
