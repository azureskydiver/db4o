package com.db4o.config.annotations.reflect;

import java.lang.annotation.*;
import java.lang.reflect.*;

import com.db4o.config.annotations.Cascade;
import com.db4o.config.annotations.CascadeType;

public class CascadeConfiguratorFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element, Annotation annotation) {
		if (!annotation.annotationType().equals(Cascade.class)) {
			return null;
		}
		String className=null;
		String fieldName=null;
		if(element instanceof Class) {
			className=((Class)element).getName();
			fieldName=null;
		}
		else if(element instanceof Field) {
			Field field=(Field)element;
			className=field.getDeclaringClass().getName();
			fieldName=field.getName();
		}
		else {
			return null;
		}
		CascadeType[] cascadeTypes = ((Cascade) annotation).value();
		return new CascadeConfigurator(className,fieldName,cascadeTypes);
	}
}
