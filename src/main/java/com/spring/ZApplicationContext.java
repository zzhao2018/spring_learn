package com.spring;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器类
 */
public class ZApplicationContext {
    private Class configClass;

    // 单例池
    ConcurrentHashMap<String, Object> singletonObejcts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ZApplicationContext(Class configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        // 获取配置类ComponentScan的注解,得到扫描路径
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        System.out.println(componentScanAnnotation.value());
        // 扫描路径下的类
        /**
         * 类加载器类型
         * Bootstrap: jre/lib下类加载器
         * Ext:jre/ext/lib下类加载器
         * App:classpath下加载器
         */
        // 获取app classLoader
        ClassLoader classLoader = ZApplicationContext.class.getClassLoader();
        // 获取类
        URL resource = classLoader.getResource(componentScanAnnotation.value().replace(".", "/"));
        System.out.println();
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                // 获取 类名
                String filename = f.getAbsolutePath();
                String classname = filename.substring(filename.indexOf("com"), filename.indexOf(".class")).replaceAll("\\\\", ".");
                try {
                    Class<?> clazz = classLoader.loadClass(classname);
                    // 发现一个类上有 componet 注解
                    if (clazz.isAnnotationPresent(Componet.class)) {
                        // 解析bean，判断当前bean是单例bean还是原型bean
                        // 若为单例bean，则放入单例池
                        // BeanDeginition
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String beanName) {
        return null;
    }
}
