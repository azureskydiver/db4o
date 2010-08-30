package com.db4o.drs.test.versant;

import java.lang.reflect.*;
import java.util.*;

public class ProxyUtil {

	public static final class InvocationHandlerImplementation<T, E extends T> implements InvocationHandler {
		private final E object;
		private final Class<T> iface;
		volatile boolean in = false;
		Object lock = new Object();
		private Set<Thread> threads = new LinkedHashSet<Thread>();
		private List<StackTraceElement[]> store = new ArrayList<StackTraceElement[]>();
		
		private InvocationHandlerImplementation(E object, Class<T> iface) {
			this.object = object;
			this.iface = iface;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("store".equals(method.getName())) {
				store.add(Thread.currentThread().getStackTrace());
			} else if ("commit".equals(method.getName())) {
				store.clear();
			} else if ("close".equals(method.getName())) {
				if (!store.isEmpty()) {
					for (StackTraceElement[] stes : store) {
						System.out.println("store");
						for (StackTraceElement ste : stes) {
							System.out.println("    " + ste);
						}
					}
					throw new IllegalStateException();
				}
			}
			System.err.println("---> " + iface.getSimpleName()+"."+method.getName() + " ("+System.identityHashCode(object)+", "+Thread.currentThread().getName()+")");
			synchronized (lock) {
				accessedFrom(Thread.currentThread());
				if (in) {
					throw new IllegalStateException("ha!");
				}
				in = true;
			}
			try {
				return method.invoke(object, args);
			} finally {
				in = false;
			}
		}

		private void accessedFrom(Thread currentThread) {
			if (threads.add(currentThread)) {
				System.err.println("----------> new thread accessing " + iface.getSimpleName());
				for (Thread t : threads) {
					System.err.println("     -> " + t.getName());
				}
			}
		}

	}

	public static <T, E extends T> T sync(Class<T> iface, final E object) {

		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				synchronized (object) {
					return method.invoke(object, args);
				}
			}
		});
	}

	public static <T, E extends T> T throwOnConcurrentAccess(final Class<T> iface, final E object) {

		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, new InvocationHandler() {

			volatile boolean in = false;
			Object lock = new Object();

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				synchronized (lock) {
					if (in) {
						throw new IllegalStateException("ha!");
					}
					in = true;
				}
				try {
					return method.invoke(object, args);
				} finally {
					in = false;
				}
			}

		});
	}
	
	public static <T, E extends T> T throwOnConcurrentAccess2(final Class<T> iface, final E object) {

		System.err.println("---> " + iface.getSimpleName()+" created ("+System.identityHashCode(object)+", "+Thread.currentThread().getName()+")");
		
		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, new InvocationHandlerImplementation(object, iface));
	}

}
