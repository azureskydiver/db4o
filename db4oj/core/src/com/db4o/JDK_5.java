/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;


class JDK_5 extends JDK_1_4 {
    
    private ReflectClass enumClass;
    
    boolean storeStaticFieldValues(Reflector reflector, ReflectClass claxx) {
        
        if(claxx == null){
            return false;
        }
        
        if(enumClass == null){
            try {
                enumClass = reflector.forClass(Class.forName(Platform.ENUM));
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        
        return enumClass.isAssignableFrom(claxx);
    }

}
