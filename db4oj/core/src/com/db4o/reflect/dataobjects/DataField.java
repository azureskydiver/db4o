/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.dataobjects;

import com.db4o.reflect.*;

public class DataField implements IField {

    private final String _name;
    private final IClass _type;

    private int _index = -1;

    public DataField(String name, IClass type) {
        _name = name;
        _type = type;
    }

    public Object get(Object onObject) {
        //TODO Consider: Do we need to check that onObject is an instance of the DataClass this field is a member of? 
        return ((DataObject)onObject)._fieldValues[_index];
    }
    
    public String getName() {
        return _name;
    }

    public IClass getType() {
        return _type;
    }

    public boolean isPublic() {
        return true;
    }

    public boolean isStatic() { //FIXME Consider static fields.
        return false;
    }

    public boolean isTransient() {
        return false;
    }

    public void set(Object onObject, Object value) {
        if (!_type.isInstance(value)) throw new RuntimeException(); //TODO Consider: is this checking really necessary?
        ((DataObject)onObject)._fieldValues[_index] = value;
    }

    public void setAccessible() {
    }

    void setIndex(int index) {
        _index = index;
    }

}
