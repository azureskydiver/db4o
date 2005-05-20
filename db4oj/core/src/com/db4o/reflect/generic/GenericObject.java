/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import java.util.*;

import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericObject {

    final GenericClass _class;
    private final Map _values;
    
    GenericObject(GenericClass clazz) {
        _class = clazz;
        _values=createValueMap(clazz);
    }
    
    void set(GenericClass owner,int index,Object value) {
    	Object[] values=(Object[])_values.get(owner);
    	values[index]=value;
    }

    Object get(GenericClass owner,int index) {
    	Object[] values=(Object[])_values.get(owner);
    	return values[index];
    }

	private Map createValueMap(GenericClass clazz) {
		Map values=new HashMap();
    	ReflectClass curclazz=clazz;
    	while(curclazz!=null) {
    		Object[] curvalues=new Object[curclazz.getDeclaredFields().length];
    		values.put(curclazz,curvalues);
    		curclazz=curclazz.getSuperclass();
    	}
    	return values;
	}

    public String toString(){
        if(_class == null){
            return super.toString();    
        }
        return "(G) " + _class.getName();
    }
}
