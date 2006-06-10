package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.db4o.config.annotations.MaximumActivationDepth;

public class MaximumActivationDepthFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(MaximumActivationDepth.class)) {
			return null;
		}
		String className=null;
		
		if(element instanceof Class) {
			className=((Class)element).getName();
		}
		
		int max= ((MaximumActivationDepth) annotation).value();
		return new MaximumActivationDepthConfigurator(className, max);
	}

}
