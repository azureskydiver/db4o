package com.db4o.config.annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class IndexedConfiguratorFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,Annotation annotation) {
		if(!(element instanceof Field)) {
			return null;
		}
		if(!(annotation instanceof Index)) {
			return null;
		}
		Field field=(Field)element;
		String className=field.getDeclaringClass().getName();
		String fieldName=field.getName();
		return new IndexedConfigurator(className,fieldName);
	}

}
