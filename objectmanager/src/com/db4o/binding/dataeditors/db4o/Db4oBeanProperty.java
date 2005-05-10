/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors.db4o;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.db4o.binding.dataeditors.IPropertyEditor;

/**
 * Db4oBeanProperty. An implementation of IProperty using dynamic proxies.
 * 
 * @author djo
 */
public class Db4oBeanProperty implements InvocationHandler {

    public static IPropertyEditor construct(Object receiver, String propertyName) throws NoSuchMethodException {
        try {
            return (IPropertyEditor) Proxy.newProxyInstance(Db4oBeanProperty.class.getClassLoader(),
                    new Class[] { IPropertyEditor.class }, new Db4oBeanProperty(
                            receiver, propertyName));
        } catch (IllegalArgumentException e) {
            throw new NoSuchMethodException(e.getMessage());
        }
    }

    private String propertyName;
    private Class propertyType;
    private Object receiver;

    private Class receiverClass;
    
    private Method setter = null;

    /**
     * Construct a JavaBeansProperty object on the specified object and property
     * 
     * @param receiver
     * @param propertyName
     */
    private Db4oBeanProperty(Object receiver, String propertyName)
            throws NoSuchMethodException {
        this.receiver = receiver;
        this.receiverClass = receiver.getClass();
        this.propertyName = propertyName;

        // There must be at least a getter...
        Method getter = receiverClass.getDeclaredMethod(realMethodName("get"), noParams);
        propertyType = getter.getReturnType();
        
        try {
            setter = receiverClass.getDeclaredMethod(
                    realMethodName("set"), new Class[] {propertyType});
        } catch (Exception e) {
            // setter = null;
        }
    }

    String realMethodName(String interfaceMethodName) {
        return interfaceMethodName.substring(0, 3) + propertyName
                + interfaceMethodName.substring(3);
    }

    private static final Class[] noParams = new Class[] {};

    /*
     * This implements a semi-relaxed duck-type over IPropertyEditor. The
     * required method is get<propertyName>. getType, getInput, and setInput
     * are implemented internally.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if ("getType".equals(method.getName())) {
            return propertyType;
        } else if ("getInput".equals(method.getName())) {
            return receiver;
        } else if ("setInput".equals(method.getName())) {
            this.receiver = args[0];
        } else if ("isReadOnly".equals(method.getName())) {
            return new Boolean(setter == null);
        } else if ("getName".equals(method.getName())) {
            return propertyName;
        } else if ("set".equals(method.getName())) {
            return setter.invoke(receiver, args);
        }

        Method realMethod;
        try {
            realMethod = receiverClass.getDeclaredMethod(
                    realMethodName(method.getName()), method.getParameterTypes());
        } catch (Exception e) {
            return null;
        }
        return realMethod.invoke(receiver, args);
    }

    public static void main(String[] args) throws Exception {
        Db4oBeanProperty o = new Db4oBeanProperty("Hello", "Name");
        System.out.println(o.realMethodName("get"));
        System.out.println(o.realMethodName("getLegalValues"));
    }

}
