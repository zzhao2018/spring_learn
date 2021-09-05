package com.demo.service;

import com.spring.Autowired;
import com.spring.Componet;

@Componet("addrService")
public class AddrService {
    @Autowired
    private CityService cityService;

    public void test() {
        System.out.println("addrService cityService:" + cityService);
    }

}
