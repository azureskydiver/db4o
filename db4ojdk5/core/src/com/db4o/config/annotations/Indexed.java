/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * turns indexing on <br> <br>
 * Example:<br> the annotation {@code  @Indexed }is used without parameter   <br><br> 
 * {@code   public class Foo  <br> @Indexed <br> private String bar; ...}
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {
	
}
