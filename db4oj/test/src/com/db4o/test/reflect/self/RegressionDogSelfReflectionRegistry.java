package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.reflect.self.*;

public class RegressionDogSelfReflectionRegistry extends SelfReflectionRegistry {
	private final static Hashtable CLASSINFO;
	
	static {
		CLASSINFO=new Hashtable(2);
		CLASSINFO.put(Animal.class, new ClassInfo(true,Object.class,new FieldInfo[]{new FieldInfo("_name",String.class)}));
		CLASSINFO.put(Dog.class, new ClassInfo(false,Animal.class,new FieldInfo[]{new FieldInfo("_age",Integer.class)}));
		//FIELDINFO.put(P1Object.class, new FieldInfo[]{});
	}
	
	public ClassInfo infoFor(Class clazz) {
		return (ClassInfo)CLASSINFO.get(clazz);
	}
}
