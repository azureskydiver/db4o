/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;


class JDK_5 extends JDK_1_4 {
    
    private static final String ENUM_CLASSNAME = "java.lang.Enum"; 
    
    private static ReflectClass enumClass;
    
    boolean isEnum(Reflector reflector, ReflectClass claxx) {
        
        if(claxx == null){
            return false;
        }
        
        if(enumClass == null){
            try {
                enumClass = reflector.forClass(Class.forName(ENUM_CLASSNAME));
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        
        return enumClass.isAssignableFrom(claxx);
    }

}
