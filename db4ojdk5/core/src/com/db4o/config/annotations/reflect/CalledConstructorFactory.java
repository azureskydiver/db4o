package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.db4o.config.annotations.CalledConstructor;

public class CalledConstructorFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(CalledConstructor.class)) {
			return null;
		}
		String className = null;

		if (element instanceof Class) {
			className = ((Class) element).getName();
		}

		boolean value = ((CalledConstructor) annotation).value();
		return new CalledConstructorConfigurator(className, value);
	}

}
