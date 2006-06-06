package com.db4o.config.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.FIELD })
public @interface Cascade {
	CascadeType[] value() default { CascadeType.ACTIVATE, CascadeType.DELETE,
			CascadeType.UPDATE };
}
