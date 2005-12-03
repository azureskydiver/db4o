package com.db4o.reflect.self;

import java.util.*;

import com.db4o.test.reflect.self.Dog;

public class UnitDogSelfReflectionRegistry implements SelfReflectionRegistry {
	private final static Hashtable FIELDINFO;
	
	static {
		FIELDINFO=new Hashtable(1);
		FIELDINFO.put(Dog.class, new SelfField[]{new SelfField("_name",String.class)});
	}
	
	public SelfField[] fieldsFor(Class clazz) {
		return (SelfField[])FIELDINFO.get(clazz);
	}
}
