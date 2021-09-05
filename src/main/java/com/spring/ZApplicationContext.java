package com.spring;

import java.io.File;
import java.lang.reflect.Field;
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

    public ZApplicationContext(Class<com.demo.AppConfig> configClass) throws Exception {
        this.configClass = configClass;
        // 扫描指定service路径，扫描类，生成beanDefine并放入map，单例bean放入单例池
        scan(configClass);
        // 扫描单例bean，放入单例池
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                singletonObejcts.put(beanName, initBean(beanDefinition));
            }
        }
        // 单例bean 依赖注入
        for (String beanName : singletonObejcts.keySet()) {
            Object obj = singletonObejcts.get(beanName);
            autowiredInject(beanName, obj);
        }
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 初始化bean
    private Object initBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void autowiredInject(String beanName, Object obj) throws Exception {
        Class clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object fieldBean = getBean(field.getName());
                if (fieldBean == null) {
                    throw new Exception("autowiredInject " + field.getName() + " bean not find");
                }
                field.setAccessible(true);
                field.set(obj, fieldBean);
            }
        }
        if (obj instanceof BeanNameAware) {
            ((BeanNameAware) obj).setBeanName(beanName);
        }
        if (obj instanceof InitializingBean) {
            ((InitializingBean) obj).afterPropertiesSet();
        }
    }

    // 创建bean
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 根据属性名称获取
                    Object bean = getBean(field.getName());
                    // private可访问
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            return instance;
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
                return createBean(beanName, beanDefinition);
            }
        } else {
            throw new Exception("bean " + beanName + " not found");
        }

    }
}
