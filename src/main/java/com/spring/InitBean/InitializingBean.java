package com.spring.InitBean;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
