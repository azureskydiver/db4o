package com.db4o.container.internal;

import java.lang.reflect.*;
import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;

import com.db4o.container.*;

public class ContainerImpl implements Container {

	public interface Binding {
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
		if (canBeServedByMe(serviceType)) {
			return new SingletonBinding(this);
		}
	    final Class<?> concreteType = Class.forName(defaultImplementationFor(serviceType));
	    final Binding newInstance = bindingFor(mostComplexConstructorFor(concreteType));
	    if (Singleton.class.isAssignableFrom(concreteType)) {
	    	return new SingletonBinding(newInstance.get());
	    }
	    return newInstance;
    }

	private boolean canBeServedByMe(Class serviceType) {
		return serviceType.isAssignableFrom(getClass());
	}
	
	static final class BindingClassLoader extends ClassLoader {

		public BindingClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class define(String className, byte[] classBytes) {
			return defineClass(className, classBytes, 0, classBytes.length);
		}
		
	}

	private Binding bindingFor(final Constructor<?> ctor) {
		if (arity(ctor) > 0)
			return new ComplexInstanceBinding(ctor);
		
		try {
			return (Binding)defineClass(bindingClassNameFor(ctor), emitClassBindingFor(ctor)).newInstance();
		} catch (SecurityException e) {
			throw new ContainerException(e);
		} catch (NoSuchMethodException e) {
			throw new ContainerException(e);
		} catch (InstantiationException e) {
			throw new ContainerException(e);
		} catch (IllegalAccessException e) {
			throw new ContainerException(e);
		}
	}

	private Class defineClass(final String className, final byte[] classBytes) {
		return bindingClassLoader().define(className.replace('/', '.'), classBytes);
	}

	private byte[] emitClassBindingFor(final Constructor<?> ctor)
			throws NoSuchMethodException {
		final String concreteTypeName = internalNameFor(ctor.getDeclaringClass());
		final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, bindingClassNameFor(ctor), null, internalNameFor(Object.class), new String[] { internalNameFor(Binding.class) });
		
		final MethodVisitor bindingCtor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getConstructorDescriptor(ctor), null, null);
		bindingCtor.visitIntInsn(Opcodes.ALOAD, 0);
		bindingCtor.visitMethodInsn(Opcodes.INVOKESPECIAL, internalNameFor(Object.class), "<init>", Type.getConstructorDescriptor(ctor));
		bindingCtor.visitInsn(Opcodes.RETURN);
		bindingCtor.visitMaxs(0, 0);
		bindingCtor.visitEnd();
		
		final MethodVisitor method = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "get", Type.getMethodDescriptor(Binding.class.getMethod("get")), null, null);
		method.visitTypeInsn(Opcodes.NEW, concreteTypeName);
		method.visitInsn(Opcodes.DUP);
		method.visitMethodInsn(Opcodes.INVOKESPECIAL, concreteTypeName, "<init>", Type.getConstructorDescriptor(ctor));
		method.visitInsn(Opcodes.ARETURN);
		method.visitMaxs(0, 0);
		method.visitEnd();
		classWriter.visitEnd();
		return classWriter.toByteArray();
	}

	private String bindingClassNameFor(final Constructor<?> ctor) {
		return ctor.getDeclaringClass().getSimpleName() + "Binding";
	}

	private BindingClassLoader bindingClassLoader() {
		return new BindingClassLoader(getClass().getClassLoader());
	}

	private String internalNameFor(final Class<?> klass) {
		return typeFor(klass).getInternalName();
	}

	private Type typeFor(final Class<?> klass) {
		return Type.getType(klass);
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
//	
//	final static class SimpleInstanceBinding implements Binding {
//
//		private static final Object[] NO_ARGS = new Object[0];
//		
//		private final Constructor<?> _parameterlessConstructor;
//
//		public SimpleInstanceBinding(Constructor<?> parameterlessConstructor) {
//			_parameterlessConstructor = parameterlessConstructor;
//        }
//
//		public Object get() {
//			try {
//	            return _parameterlessConstructor.newInstance(NO_ARGS);
//            } catch (InstantiationException e) {
//            	throw new ContainerException(e);
//            } catch (IllegalAccessException e) {
//            	throw new ContainerException(e);
//            } catch (IllegalArgumentException e) {
//            	throw new ContainerException(e);
//			} catch (InvocationTargetException e) {
//				throw new ContainerException(e);
//			}
//        }
//	}
	
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

