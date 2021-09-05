package com.demo;

import com.spring.ZApplicationContext;

public class Test {
    public static void main(String[] args) throws Exception {
        ZApplicationContext applicationContext = new ZApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));

    }
}
