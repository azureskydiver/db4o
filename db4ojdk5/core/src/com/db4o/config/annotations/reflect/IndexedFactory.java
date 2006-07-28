/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import com.db4o.config.annotations.Indexed;

public class IndexedFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(Indexed.class)) {
			return null;
		}
		String fieldName=null;
		String className=null;
		if(element instanceof Field) {
			Field field=(Field)element;
			fieldName=field.getName();
			className=field.getDeclaringClass().getName();
		}
		else {
			return null;
		}
		
		return new IndexedConfigurator(className,fieldName);
	}

}
