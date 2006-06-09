package com.db4o.config.annotations;

import java.lang.reflect.*;

public class ConfigurationFactoryUtil {

	public static String classNameFromClass(AnnotatedElement element) {
		if(!(element instanceof Class)) {
			throw new RuntimeException("Expected to be called for class.");
		}
		return ((Class)element).getName();
	}
}
