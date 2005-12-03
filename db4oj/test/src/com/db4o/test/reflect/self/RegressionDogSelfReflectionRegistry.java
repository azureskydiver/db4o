package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.*;
import com.db4o.reflect.self.*;

public class RegressionDogSelfReflectionRegistry implements SelfReflectionRegistry {
	private final static Hashtable FIELDINFO;
	
	static {
		FIELDINFO=new Hashtable(2);
		FIELDINFO.put(Dog.class, new SelfField[]{new SelfField("_name",String.class)});
		FIELDINFO.put(P1Object.class, new SelfField[]{});
	}
	
	
	
	public SelfField[] fieldsFor(Class clazz) {
		return (SelfField[])FIELDINFO.get(clazz);
	}

	public boolean isKnownClass(Class clazz) {
		return FIELDINFO.get(clazz)!=null;
	}
}
