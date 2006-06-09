package com.db4o.config.annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class CallConstructorConfiguratorFactory implements Db4oConfiguratorFactory {
	public Db4oConfigurator configuratorFor(AnnotatedElement element, Annotation annotation) {
		if(!(annotation instanceof CallConstructor)) {
			return null;
		}
		String className = ConfigurationFactoryUtil.classNameFromClass(element);
		return new CallConstructorConfigurator(className);
	}
}
