package com.db4o.reflect.dataobjects;

import com.db4o.*;
import com.db4o.reflect.*;

public class DataObjectReflector implements IReflect {

    private final Hashtable4 _iClassByNativeClass = new Hashtable4(1);

    public IArray array() {
        return null;
    }

    public boolean constructorCallsSupported() {
        return false;
    }

    public IClass forName(String className) throws ClassNotFoundException {
        return null;
    }

    public IClass forClass(Class clazz) {
        IClass result = (IClass)_iClassByNativeClass.get(clazz);
        if (result == null) {
            result = new DataObjectClass(clazz);
            _iClassByNativeClass.put(clazz, result);
        }
        return result;
    }

    public IClass forObject(Object a_object) {
        return null;
    }

}
