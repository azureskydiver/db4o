/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * generate version numbers for stored objects of this class.
 * @exclude
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedVersionNumbers {
	boolean value() default true;
}
