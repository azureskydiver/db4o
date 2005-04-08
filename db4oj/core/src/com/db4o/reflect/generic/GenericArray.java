/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.generic;

/**
 * @exclude
 */
public class GenericArray extends GenericObject{
    
    public GenericArray(GenericClass clazz, int size){
        super(clazz, size);
    }
    
    int getLength(){
        return _values.length;
    }
    

}
