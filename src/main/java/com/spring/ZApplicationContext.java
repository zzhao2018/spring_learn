package com.spring;

/**
 * 容器类
 */
public class ZApplicationContext {
    private Class configClass;

    public ZApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 解析配置类
        // 解析ComponentScan注解
        // 扫描路径
        // 扫描

    }

    public Object getBean(String beanName) {
        return null;
    }
}
