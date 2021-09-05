package com.spring;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器类
 */
public class ZApplicationContext {
    private Class<com.demo.AppConfig> configClass;

    // 单例池
    ConcurrentHashMap<String, Object> singletonObejcts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ZApplicationContext(Class<com.demo.AppConfig> configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        // 扫描指定service路径，扫描类，生成beanDefine并放入map，单例bean放入单例池
        scan(configClass);
    }

    private void scan(Class<com.demo.AppConfig> configClass) {
        // 获取配置类ComponentScan的注解,得到扫描路径
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);

        // 获取app classLoader
        ClassLoader classLoader = ZApplicationContext.class.getClassLoader();
        // 获取类
        URL resource = classLoader.getResource(componentScanAnnotation.value().replace(".", "/"));
        assert resource != null;
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String filename = f.getAbsolutePath();
                if (filename.endsWith(".class")) {
                    String classname = filename.substring(filename.indexOf("com"), filename.indexOf(".class")).replaceAll("\\\\", ".");
                    try {
                        Class<?> clazz = classLoader.loadClass(classname);
                        // 判断类上是否有 componet 注解
                        if (clazz.isAnnotationPresent(Componet.class)) {
                            Componet componet = clazz.getDeclaredAnnotation(Componet.class);
                            String beanName = componet.value();
                            // 初始化BeanDefinition
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scope = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scope.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                            // 如果是单例bean，放入单例池
                            if (beanDefinition.getScope().equals("singleton")) {
                                singletonObejcts.put(beanName, createBean(beanDefinition));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) throws Exception {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonObejcts.get(beanName);
            } else {
                return createBean(beanDefinition);
            }
        } else {
            throw new Exception("bean " + beanName + " not found");
        }

    }
}
