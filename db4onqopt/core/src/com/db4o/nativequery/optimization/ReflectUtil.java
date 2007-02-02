/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.internal.*;

public class ReflectUtil {
	public static Method methodFor(Class clazz, String methodName, Class[] paramTypes) {
		Class curclazz=clazz;
		while(curclazz!=null) {
			try {
				Method method=curclazz.getDeclaredMethod(methodName, paramTypes);
				Platform4.setAccessible(method);
				return method;
			} catch (Exception e) {
			}
			curclazz=curclazz.getSuperclass();
		}
		return null;
	}

	public static Field fieldFor(final Class clazz,final String name) {
		Class curclazz=clazz;
		while(curclazz!=null) {
			try {
				Field field=curclazz.getDeclaredField(name);
				Platform4.setAccessible(field);
				return field;
			} catch (Exception e) {
			}
			curclazz=curclazz.getSuperclass();
		}
		return null;
	}

}
