package com.db4o.foundation;

import java.util.*;

import com.db4o.internal.*;

/**
 * @sharpen.partial
 */
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

	public static Environment newConventionBasedEnvironment() {
		return new ConventionBasedEnvironment();
    }
	
	private static final class ConventionBasedEnvironment implements Environment {
	    private final Map<Class<?>, Object> _bindings = new HashMap<Class<?>, Object>();

	    public <T> T provide(Class<T> service) {
	        final Object existing = _bindings.get(service);
	        if (null != existing) {
	        	return service.cast(existing);
	        }
	        final T binding = resolve(service);
	        _bindings.put(service, binding);
	        return binding;
	    }
	    
	    private <T> T resolve(Class<T> service) {
	    	final String className = defaultImplementationFor(service);
			return service.cast(ReflectPlatform.createInstance(className));
	    }
    }

	/**
	 * @sharpen.ignore
	 */
	static String defaultImplementationFor(Class service) {
		final String packageName = service.getPackage().getName();
		final int lastPackage = packageName.lastIndexOf('.');
		return packageName.substring(0, lastPackage) + ".internal" + packageName.substring(lastPackage) + "." + ReflectPlatform.simpleName(service) + "Impl";
	}
}
