/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

/**
 * @exclude
 */
public class GenericObject {

    final GenericClass _class;
    final Object[] _values;
    
    GenericObject(GenericClass clazz, int length) {
        _class = clazz;
        _values = new Object[length];
    }
    
    GenericObject(GenericClass clazz) {
        this(clazz, clazz.getDeclaredFields().length);
    }

    public String toString(){
        if(_class == null){
            return super.toString();    
        }
        return "(G) " + _class.getName();
    }

}
