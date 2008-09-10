package com.db4o.container.internal;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.container.*;

public class ContainerImpl implements Container {

	interface Binding {
		Object get();
	}
	
	private final Map<Class, Binding> _serviceBindingCache = new HashMap<Class, Binding>();

	public <T> T produce(Class<T> serviceType) {
		final Binding binding = bindingFor(serviceType);
		return (T) binding.get();
    }

	private Binding bindingFor(Class serviceType) {
		final Binding cached = _serviceBindingCache.get(serviceType);
		if (null != cached) return cached;
		try {
	        final Binding binding = resolve(serviceType);
	        _serviceBindingCache.put(serviceType, binding);
	        return binding;
		} catch (ClassNotFoundException e) {
        	throw new ContainerException(e);
        }
	}

	protected Binding resolve(Class serviceType) throws ClassNotFoundException {
		if (serviceType.isAssignableFrom(getClass())) {
			return new SingletonBinding(this);
		}
	    final Class<?> concreteType = Class.forName(defaultImplementationFor(serviceType));
	    final Constructor<?> mostComplexConstructor = mostComplexConstructorFor(concreteType);
	    final Binding newInstance = arity(mostComplexConstructor) > 0 ? new ComplexInstanceBinding(mostComplexConstructor) : new SimpleInstanceBinding(concreteType);
	    if (Singleton.class.isAssignableFrom(concreteType)) {
	    	return new SingletonBinding(newInstance.get());
	    }
	    return newInstance;
    }

	private int arity(final Constructor<?> ctor) {
	    return ctor.getParameterTypes().length;
    }

	private Constructor<?> mostComplexConstructorFor(final Class<?> concreteType) {
	    final Constructor<?>[] ctors = concreteType.getDeclaredConstructors();
	    Arrays.sort(ctors, new Comparator<Constructor>() {
			public int compare(Constructor x, Constructor y) {
				return arity(y) - arity(x);
			}
		});
	    return ctors[0];
    }

	private <T> String defaultImplementationFor(Class<T> serviceType) {
	    return serviceType.getPackage().getName() + ".internal." + serviceType.getSimpleName() + "Impl";
    }
	
	final static class SingletonBinding implements Binding {
		private final Object _instance;

		public SingletonBinding(Object instance) {
			_instance = instance;
        }

		public Object get() {
			return _instance;
		}
	}
	
	final static class SimpleInstanceBinding implements Binding {

		private final Class<?> _concreteType;

		public SimpleInstanceBinding(Class<?> concreteType) {
			_concreteType = concreteType;
        }

		public Object get() {
			try {
	            return _concreteType.newInstance();
            } catch (InstantiationException e) {
            	throw new ContainerException(e);
            } catch (IllegalAccessException e) {
            	throw new ContainerException(e);
            }
        }
	}
	
	final class ComplexInstanceBinding implements Binding {
		private final Constructor<?> _constructor;

		ComplexInstanceBinding(Constructor<?> constructor) {
		    _constructor = constructor;
	    }

	    public Object get() {
	    	try {
	    		final Object[] args = produceAll(_constructor.getParameterTypes());
	    		return _constructor.newInstance(args);
	    	} catch (InstantiationException e) {
	        	throw new ContainerException(e);
	        } catch (IllegalAccessException e) {
	        	throw new ContainerException(e);
	        } catch (IllegalArgumentException e) {
	        	throw new ContainerException(e);
            } catch (InvocationTargetException e) {
            	throw new ContainerException(e);
            }
	    }

		private Object[] produceAll(final Class<?>[] types) {
	        final Object[] args = new Object[types.length];
	        for (int i=0; i<types.length; ++i) {
	        	args[i] = produce(types[i]);
	        }
	        return args;
        }
	}
}
