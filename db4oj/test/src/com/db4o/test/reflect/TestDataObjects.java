/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.reflect;

import com.db4o.reflect.*;
import com.db4o.reflect.dataobjects.*;

public class TestDataObjects extends Test {

	private final DataObjectReflector _reflector;
	private IClass _classReflector;
	
	TestDataObjects(DataObjectReflector reflector) throws ClassNotFoundException {
        _reflector = reflector;
        out("Data Object test started...");
		test();
		out("----------------------------------------------------------");
	}

	private void test() throws ClassNotFoundException {
        _reflector.registerDataClass(acmeDataClass());
        IClass claxx = _reflector.forName("com.acme.Person");
        _assert(claxx.getName().equals("com.acme.Person"));
        
        
//      IField[] fields = claxx.getDeclaredFields();
//      IField surname getDeclaredField(String name);
//      _assert(fields[0] == = claxx.getDeclaredFields();
        

	}

    private DataClass acmeDataClass() {
        DataClass result = new DataClass("com.acme.Person");
        //result.registerField();
        return result;
    }

}
