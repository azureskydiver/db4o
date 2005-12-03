package com.db4o.reflect.self;

import java.util.*;

public class UnitDogSelfReflectionRegistry implements SelfReflectionRegistry {
	private final static Hashtable FIELDINFO;
	
	static {
		FIELDINFO=new Hashtable(1);
		FIELDINFO.put(Dog.class, new FieldInfo[]{new FieldInfo("_name",String.class)});
	}
	
	public FieldInfo[] fieldsFor(Class clazz) {
		return (FieldInfo[])FIELDINFO.get(clazz);
	}
}
