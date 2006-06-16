package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import com.db4o.config.annotations.Cascade;
import com.db4o.config.annotations.CascadeType;
import com.db4o.config.annotations.Index;

public class IndexedFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(Index.class)) {
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
