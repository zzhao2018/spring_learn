package com.demo;

import com.spring.ZApplicationContext;

public class Test {
    public static void main(String[] args) {
        ZApplicationContext applicationContext = new ZApplicationContext(AppConfig.class);

        Object userService=applicationContext.getBean("userService");
    }
}