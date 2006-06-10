package com.db4o.config.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.db4o.config.annotations.UpdateDepth;

public class UpdateDepthFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		
		if (!annotation.annotationType().equals(UpdateDepth.class)) {
			return null;
		}
		String className=null;
		
		if(element instanceof Class) {
			className=((Class)element).getName();
		}
		
		int updateDepth= ((UpdateDepth) annotation).value();
		return new UpdateDepthConfigurator(className, updateDepth);
	}

}
