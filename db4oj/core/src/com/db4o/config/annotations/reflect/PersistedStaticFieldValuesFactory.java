/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations.reflect;

import java.lang.annotation.*;
import java.lang.reflect.*;

import com.db4o.config.annotations.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore
public class PersistedStaticFieldValuesFactory implements Db4oConfiguratorFactory {

	public Db4oConfigurator configuratorFor(AnnotatedElement element,
			Annotation annotation) {
		if (!annotation.annotationType().equals(PersistedStaticFieldValues.class)) {
			return null;
		}
		String className = null;

		if (element instanceof Class) {
			className = ((Class) element).getName();
		}

		return new PersistedStaticFieldValuesConfigurator(className);
	}

}
