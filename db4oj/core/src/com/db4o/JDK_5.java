/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;


/**
 * 
 */
public class JDK_5 extends JDK_1_4 {
    
    private Class enumClass;
    
    boolean storeStaticFieldValues(Class clazz) {
        if(enumClass == null){
            try {
                enumClass = Class.forName(Platform.ENUM);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return enumClass.isAssignableFrom(clazz);
    }

}
