package com.demo.test;

import com.spring.di.Autowired;
import com.spring.ioc.Componet;

@Componet("addrService")
public class AddrService {
    @Autowired
    private CityService cityService;

    public void test() {
        System.out.println("addrService cityService:" + cityService);
    }

}
