package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.db4o.config.annotations.GeneratedUUIDs;

public class GeneratedUUIDsFactory implements
		Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(GeneratedUUIDs.class)) {
			return null;
		}
		String className = null;

		if (element instanceof Class) {
			className = ((Class) element).getName();
		}

		boolean value = ((GeneratedUUIDs) annotation).value();
		return new GeneratedUUIDsConfigurator(className, value);
	}

}
