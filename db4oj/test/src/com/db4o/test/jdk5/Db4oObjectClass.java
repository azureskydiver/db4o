package com.db4o.test.jdk5;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Db4oObjectClass {
	boolean cascadeOnActivate() default false;
	boolean cascadeOnUpdate() default false;
	boolean cascadeOnDelete() default false;
	int minimumActivationDepth() default -1;
	int maximumActivationDepth() default -1;
}
