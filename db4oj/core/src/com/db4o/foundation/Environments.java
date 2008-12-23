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
	    
	    /**
	     * Resolves a service interface to its default implementation using the
	     * db4o namespace convention:
	     * 
	     *      interface foo.bar.Baz
	     *      default implementation foo.internal.bar.BazImpl
	     *
	     * @return the convention based type name for the requested service
	     */
	    private <T> T resolve(Class<T> service) {
	    	final String className = defaultImplementationFor(service);
	    	final Object binding = ReflectPlatform.createInstance(className);
	    	if (null == binding) {
	        	throw new IllegalArgumentException("Cant find default implementation for " + service.toString() + ": " + className);
	        }
			return service.cast(binding);
	    }
    }

	/**
	 * @sharpen.ignore
	 */
	static String defaultImplementationFor(Class service) {
		final String packageName = splitQualifiedName(service.getName()).qualifier;
		final QualifiedName packageParts = splitQualifiedName(packageName);
		return packageParts.qualifier + ".internal" + packageParts.name + "." + ReflectPlatform.simpleName(service) + "Impl";
	}
	
	/**
	 * @sharpen.ignore
	 */
	private static final class QualifiedName {
		final String qualifier;
		final String name;

		public QualifiedName(String qualifier, String name) {
	        this.qualifier = qualifier;
	        this.name = name;
        }
	}

	/**
	 * @sharpen.ignore
	 */
	private static QualifiedName splitQualifiedName(final String qualifiedName) {
	    final int lastDot = qualifiedName.lastIndexOf('.');
		final String qualifier = qualifiedName.substring(0, lastDot);
		final String name = qualifiedName.substring(lastDot);
		return new QualifiedName(qualifier, name);
    }
}
