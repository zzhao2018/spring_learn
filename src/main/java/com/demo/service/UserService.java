package com.demo.service;

import com.spring.*;

@Componet("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet ----");
    }
}
