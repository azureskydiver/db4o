/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.reflect;

import com.db4o.foundation.*;


/**
 * convenience class for often used operations on the reflection layer.
 */
public class Reflections {
	
	public static ReflectField field(ReflectClass claxx, String name){
		while(claxx!=null) {
			try {
				return claxx.getDeclaredField(name);
			} catch (Exception e) {
				
			}
			claxx=claxx.getSuperclass();
		}
		return null;
	}
	
	public static void forEachField(ReflectClass claxx, Procedure4<ReflectField> procedure){
		while(claxx!=null) {
			final ReflectField[] declaredFields = claxx.getDeclaredFields();
			for (ReflectField reflectField : declaredFields) {
				procedure.apply(reflectField);
			}
			claxx=claxx.getSuperclass();
		}
	}
	
}
