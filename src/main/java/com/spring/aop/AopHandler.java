package com.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AopHandler implements InvocationHandler {
    private Class<?> clazz;
    private Object instance;
    private Object aopInstance;
    private Map<String, List<Method>> beforeMethodMap = new ConcurrentHashMap<>();
    private Map<String, List<Method>> afterMethodMap = new ConcurrentHashMap<>();


    public AopHandler(Class aopClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.clazz = aopClass;
        this.aopInstance = aopClass.getDeclaredConstructor().newInstance();
        Method[] aopMethods = clazz.getDeclaredMethods();
        for (Method aopMethod : aopMethods) {
            if (aopMethod.isAnnotationPresent(Before.class)) {
                List<Method> aopMethodList;
                String methodName = aopMethod.getAnnotation(Before.class).value();
                if (beforeMethodMap.containsKey(methodName)) {
                    aopMethodList = beforeMethodMap.get(aopMethod.getName());
                } else {
                    aopMethodList = new ArrayList<>();
                }
                aopMethodList.add(aopMethod);
                beforeMethodMap.put(methodName, aopMethodList);
            } else if (aopMethod.isAnnotationPresent(After.class)) {
                List<Method> aopMethodList;
                String methodName = aopMethod.getAnnotation(After.class).value();
                if (afterMethodMap.containsKey(methodName)) {
                    aopMethodList = afterMethodMap.get(aopMethod.getName());
                } else {
                    aopMethodList = new ArrayList<>();
                }
                aopMethodList.add(aopMethod);
                afterMethodMap.put(methodName, aopMethodList);
            }
        }
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (beforeMethodMap.containsKey(method.getName())) {
            for (Method beforeMethod : beforeMethodMap.get(method.getName())) {
                beforeMethod.invoke(aopInstance);
            }
        }
        Object result = method.invoke(this.instance, args);
        if (afterMethodMap.containsKey(method.getName())) {
            for (Method afterMethod : afterMethodMap.get(method.getName())) {
                afterMethod.invoke(aopInstance);
            }
        }
        return result;
    }
}
