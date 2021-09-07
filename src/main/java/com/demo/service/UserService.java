package com.demo.service;

import com.spring.*;

@Componet("userService")
//@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {
    @Autowired
    private AddrService addrService;

    private String beanName;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void test() {
        System.out.println(addrService);
        addrService.test();
//        System.out.println("name:" + name);

    }

    @Override
    public void setBeanName(String var1) {
        this.beanName = var1;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        System.out.println("afterPropertiesSet ----");
    }
}
