/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.dataobjects;


class DataObject {

    private final DataClass _class;
    final Object[] _fieldValues;
    
    DataObject(DataClass dataClass) {
        _class = dataClass;
        _fieldValues = new Object[_class.getDeclaredFields().length];
    }

    DataClass getDataClass() {
        return _class;
    }

}
