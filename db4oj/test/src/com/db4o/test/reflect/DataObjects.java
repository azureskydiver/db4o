/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.reflect;

import java.util.*;

import com.db4o.reflect.*;
import com.db4o.reflect.dataobjects.*;
import com.db4o.reflect.jdk.*;

public class DataObjects extends Test {

	private final DataObjectReflector _reflector;
    private final IClass _objectIClass;

    private IClass _iClass;
	
	public DataObjects() throws ClassNotFoundException {
        _reflector = new DataObjectReflector(new CReflect(Thread.currentThread().getContextClassLoader()));
        _objectIClass = _reflector.forClass(Object.class);
	}

	public void test() throws ClassNotFoundException {
        _reflector.registerDataClass(acmeDataClass());
        _iClass = _reflector.forName("com.acme.Person");
        _assert(_iClass.getName().equals("com.acme.Person"));
        _assert(_iClass.getSuperclass() == _objectIClass);
        
        _assert(_iClass.isAssignableFrom(subclass()));
        _assert(!_iClass.isAssignableFrom(otherDataClass()));
        _assert(!_iClass.isAssignableFrom(_objectIClass));
    
        _assert(_iClass.isInstance(_iClass.newInstance()));
        _assert(_iClass.isInstance(subclass().newInstance()));
        _assert(!_iClass.isInstance(otherDataClass().newInstance()));
        _assert(!_iClass.isInstance("whatever")); 
        
        tstFields();

	}

    private DataClass otherDataClass() {
        return new DataClass("anyName", _objectIClass, new DataField[0]);
    }

    private DataClass subclass() {
        return new DataClass("anyName", _iClass, new DataField[0]);
    }

    private void tstFields() {
        IField surname = _iClass.getDeclaredField("surname");
        IField birthdate = _iClass.getDeclaredField("birthdate");
        IField[] fields = _iClass.getDeclaredFields();
        _assert(fields.length == 2);
        _assert(fields[0] == surname);
        _assert(fields[1] == birthdate);
        
        Object person = _iClass.newInstance();
        _assert(birthdate.get(person) == null);
        surname.set(person, "Cleese");
        _assert(surname.get(person).equals("Cleese"));
    }

    private DataClass acmeDataClass() {
        DataClass result = new DataClass("com.acme.Person", _objectIClass, fields());
        return result;
    }

    private DataField[] fields() {
        return new DataField[] {
                new DataField("surname", _reflector.forClass(String.class)),
                new DataField("birthdate", _reflector.forClass(Date.class))
        };
    }

}
