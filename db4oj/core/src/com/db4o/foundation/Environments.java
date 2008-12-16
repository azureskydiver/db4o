package com.db4o.foundation;

public class Environments {
	
	private static final DynamicVariable<Environment> _current = DynamicVariable.newInstance();
	
	public static <T> T my(Class<T> service) {
		final Environment environment = _current.value();
		if (null == environment) {
			throw new IllegalStateException();
		}
		return environment.provide(service);
	}
	
	public static void runWith(Environment environment, Runnable runnable) {
		_current.with(environment, runnable);
	}

}
