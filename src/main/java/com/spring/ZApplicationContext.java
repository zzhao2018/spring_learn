package com.spring;

import com.spring.InitBean.BeanNameAware;
import com.spring.InitBean.BeanPostProcessor;
import com.spring.InitBean.InitializingBean;
import com.spring.aop.AopHandler;
import com.spring.aop.Aspect;
import com.spring.di.Autowired;
import com.spring.ioc.BeanDefinition;
import com.spring.ioc.ComponentScan;
import com.spring.ioc.Componet;
import com.spring.ioc.Scope;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器类
 */
public class ZApplicationContext {
    private Class<com.demo.AppConfig> configClass;

    // 单例池
    ConcurrentHashMap<String, Object> singletonObejcts = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    List<AopHandler> aopHandlers = new ArrayList<>();

    public ZApplicationContext(Class<com.demo.AppConfig> configClass) throws Exception {
        this.configClass = configClass;
        // 扫描指定service路径，扫描类，生成beanDefine并放入map，单例bean放入单例池
        scan(configClass);
        // 扫描单例bean，放入单例池
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                if (!singletonObejcts.containsKey(beanName)) {
                    singletonObejcts.put(beanName, createBean(beanName, beanDefinition));
                }
            }
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
                            // 切面
                            if (clazz.isAnnotationPresent(Aspect.class)) {
                                aopHandlers.add(new AopHandler(clazz));
                                continue;
                            }
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                beanPostProcessors.add((BeanPostProcessor) clazz.getDeclaredConstructor().newInstance());
                            }

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

    // 创建bean
    private Object createBean(String beanName, BeanDefinition beanDefinition) throws Exception {
        if (singletonObejcts.contains(beanName)) {
            return singletonObejcts.get(beanName);
        }
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 根据属性名称获取
                    Object bean = getBean(field.getName());
                    if (bean == null) {
                        // 获取依赖，创建bean
                        BeanDefinition fieldBeanDefinition = beanDefinitionMap.get(field.getName());
                        if (fieldBeanDefinition != null) {
                            Object dependObj = createBean(field.getName(), fieldBeanDefinition);
                            if (fieldBeanDefinition.getScope().equals("singleton")) {
                                singletonObejcts.put(field.getName(), dependObj);
                            }
                            bean = getBean(field.getName());
                        } else {
                            throw new Exception(beanName + " depend on " + field.getName() + " is null");
                        }
                    }
                    // private可访问
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // beanPostProcessor 初始化前动作
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 初始化bean
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // beanPostProcessor 初始化后动作
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
            // aop粗略实现 todo
            Class superInterface = null;
            for (Class interfacez : clazz.getInterfaces()) {
                if ((interfacez.getName() + "Impl").equals(clazz.getName())) {
                    superInterface = interfacez;
                    break;
                }
            }
            for (AopHandler handler : aopHandlers) {
                handler.setInstance(instance);
                instance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{superInterface}, handler);
            }
            return instance;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
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
