package com.db4o.rmi;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

class Request {

	private Object value;
	private boolean done = false;
	private Method method;
	private Object[] args;
	private List<Callback<?>> callbacks;
	private final Distributor<?> distributor;
	private Object object;

	public Request(Distributor<?> distributor, Method method, Object[] args) {
		this.distributor = distributor;
		this.method = method;
		this.args = args;
	}

	public Request(Distributor<?> distributor, Object object) {
		this.distributor = distributor;
		this.object = object;
	}

	public synchronized Object get() throws InterruptedException {
		if (!done) {
			wait();
		}
		return value;
	}

	public Object getInternal() {
		return value;
	}

	public synchronized void set(Object value) {
		this.value = value;
		done = true;
		notifyAll();
		if (callbacks != null) {
			for (Callback callback : callbacks) {
				callback.returned(value);
			}
			callbacks = null;
		}
	}

	public synchronized void addCallback(Callback callback) {
		if (done) {
			callback.returned(value);
			return;
		}
		if (callbacks == null) {
			callbacks = new ArrayList<Callback<?>>();
		}
		callbacks.add(callback);
	}

	public void serialize(DataOutput out) throws IOException {

		out.writeUTF(method.getName());
		Class<?>[] paramTypes = method.getParameterTypes();
		out.writeByte(paramTypes.length);

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> t = paramTypes[i];
			out.writeUTF(t.getName());
			Object o = args[i];
			if (o == null) {
				out.writeBoolean(false);
				continue;
			}
			out.writeBoolean(true);

			boolean proxy = hasProxyAnnotation(method.getParameterAnnotations()[i]);

			out.writeBoolean(proxy);
			if (proxy) {

				PeerServer server = distributor.serverFor(o);

				out.writeLong(server.getId());
				out.writeUTF(t.getName());

			} else {

				serializerFor(t).serialize(out, o);
			}
		}
	}
	
	public static Serializer<Object> serializerFor(String className) {
		Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(classForName(className));
		if (s == null) {
			throw new RuntimeException("No serializer registered for: " + className);
		}
		return s;
	}

	public static Serializer<Object> serializerFor(Class<?> t) {
		Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(t);
		if (s == null) {
			throw new RuntimeException("No serializer registered for: " + t);
		}
		return s;
	}

	public static boolean hasProxyAnnotation(Annotation[] anns) {
		for (Annotation ann : anns) {
			if (com.db4o.rmi.test.Proxy.class == ann.annotationType()) {
				return true;
			}
		}
		return false;
	}

	public void deserialize(DataInput in) throws IOException {

		String name = in.readUTF();
		Class<?>[] paramTypes = new Class<?>[in.readByte()];
		args = new Object[paramTypes.length];

		for (int i = 0; i < paramTypes.length; i++) {

			Class<?> t = classForName(in.readUTF());
			paramTypes[i] = t;

			if (!in.readBoolean()) {
				args[i] = null;
				continue;
			}

			boolean proxy = in.readBoolean();

			if (proxy) {

				long id = in.readLong();
				Class<?> clazz = classForName(in.readUTF());

				args[i] = distributor.proxyFor(id, clazz).sync();

			} else {

				args[i] = serializerFor(t).deserialize(in);
			}
		}
		resolveMethod(name, paramTypes);
	}

	private void resolveMethod(String name, Class<?>[] paramTypes) {
		try {
			method = object.getClass().getMethod(name, paramTypes);
			method.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> classForName(String className) {
		try {
			return ClassResolver.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void invoke() {
		try {
			value = method.invoke(object, args);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasValue() {
		return done;
	}

}
