/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

/**
 * @exclude
 */
class GenericObject {

    private final GenericClass _class;
    final Object[] _fieldValues;
    
    GenericObject(GenericClass dataClass) {
        _class = dataClass;
        _fieldValues = new Object[_class.getDeclaredFields().length];
    }

    GenericClass genericClass() {
        return _class;
    }

}
