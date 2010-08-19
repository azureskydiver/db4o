package com.db4o.rmi;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;


public class SimplePeer<T> implements Peer<T> {

	private static final int REQUEST = 0;
	private static final int RESPONSE = 1;

	private AtomicLong nextRequest = new AtomicLong();
	
	private ConcurrentMap<Long, Request> requests = new ConcurrentHashMap<Long,Request>();

	private DataOutputStream out;

	private T syncFacade;

	private T asyncFacade;

	private T object;
	private Class<T> clazz;
	private Runnable feeder;
	
	private class Request {

		private Object value;
		private boolean done = false;
		private Method method;
		private Object[] args;
		private List<Callback<?>> callbacks;

		public Request(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}

		public Request() {
			// TODO Auto-generated constructor stub
		}

		public synchronized Object get() throws InterruptedException {
			if (!done) {
				wait();
			}
			return value;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public synchronized void set(Object value) {
			this.value = value;
			done = true;
			notifyAll();
			if (callbacks != null) {
				for(Callback callback : callbacks) {
					callback.returned(value);
				}
				callbacks = null;
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
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
				@SuppressWarnings("unchecked")
				Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(t);
				s.serialize(out, o);
			}
		}

		public void deserialize(DataInput in) throws IOException {
			String name = in.readUTF();
			Class<?>[] paramTypes = new Class<?>[in.readByte()];
			args = new Object[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				Class<?> t;
				try {
					t = classForName(in.readUTF());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				paramTypes[i] = t;
				if (!in.readBoolean()) {
					args[i] = null;
					continue;
				}
				@SuppressWarnings("unchecked")
				Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(t);
				Object o = s.deserialize(in);
				args[i] = o;
			}
			try {
				method = object.getClass().getMethod(name, paramTypes);
				method.setAccessible(true);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		
		

		public void invoke() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			value = method.invoke(object, args);
		}

		public boolean hasValue() {
			return done;
		}
		
	}

	private static final Map<String,Class<?>> builtInMap = new HashMap<String,Class<?>>();
	
	static {
		builtInMap.put("int", Integer.TYPE );
		builtInMap.put("long", Long.TYPE );
		builtInMap.put("double", Double.TYPE );
		builtInMap.put("float", Float.TYPE );
		builtInMap.put("bool", Boolean.TYPE );
		builtInMap.put("char", Character.TYPE );
		builtInMap.put("byte", Byte.TYPE );
		builtInMap.put("void", Void.TYPE );
		builtInMap.put("short", Short.TYPE );
	}
	
	public static Class<?> classForName(String className) throws ClassNotFoundException {
		Class<?> clazz = builtInMap.get(className);
		if (clazz != null) {
			return clazz;
		}
		return Class.forName(className);
	}
	
	public SimplePeer(ByteArrayConsumer consumer, Class<T> clazz) {
		this.clazz = clazz;
		if (consumer != null) setConsumer(consumer);
	}

	public void setConsumer(final ByteArrayConsumer consumer) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() {
			@Override
			public void flush() throws IOException {
				super.flush();
				consumer.consume(this.buf, 0, this.count);
				reset();
			}
		};
		this.out = new DataOutputStream(bout);
	}
	
	public SimplePeer(ByteArrayConsumer consumer, T object) {
		this.object = object;
		if (consumer != null) setConsumer(consumer);
	}
	
	@SuppressWarnings("unchecked")
	public T sync() {
		if (syncFacade == null) {
			syncFacade = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
				
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Request r = request(method, args);
					if (!r.hasValue() && getFeeder() != null) {
						getFeeder().run();
					}
					return r.get();
				}
			});
		}
		return syncFacade;
	}
	
	protected Request request(Method method, Object[] args) {
		Request r = new Request(method, args);
		long id = nextRequest.getAndIncrement();
		requests.put(id, r);
		
		try {
			synchronized (out) {
				out.writeByte(REQUEST);
				out.writeLong(id);
				r.serialize(out);
				out.writeBoolean(false);
				out.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return r;
	}
	
	public void consume(byte[] buffer, int offset, int length) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, offset, length));
		do {
			processOne(in);
			
		} while(in.readBoolean());
	}

	private void processOne(DataInputStream in) throws IOException {

		byte op = in.readByte();
		
		switch (op) {
		case REQUEST:
			processRequest(in);
			break;
			
		case RESPONSE:
			processResponse(in);
			break;

		default:
			throw new RuntimeException("Unknown operation: "+ op);
		}
		
	}

	private void processResponse(DataInputStream in) throws IOException {
		long id = in.readLong();
		Request r = requests.remove(id);
		
		if (r == null) {
			throw new IllegalStateException("Request " + id + " is unknown");
		}
		
		Object o = null;
		if (in.readBoolean()) {
			try {
				@SuppressWarnings("unchecked")
				Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(Class.forName(in.readUTF()));
				o = s.deserialize(in);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		r.set(o);
	}

	private void processRequest(DataInputStream in) throws IOException {
		long id = in.readLong();
		Request r = new Request();
		r.deserialize(in);
		try {
			r.invoke();
		} catch (IllegalArgumentException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		} catch (InvocationTargetException e) {
			throw new IOException(e);
		}
		synchronized (out) {
			out.writeByte(RESPONSE);
			out.writeLong(id);
			Object ret = r.value;
			if (ret == null) {
				out.writeBoolean(false);
			} else {
				out.writeBoolean(true);
				Class<? extends Object> clazz = ret.getClass();
				out.writeUTF(clazz.getName());
				@SuppressWarnings("unchecked")
				Serializer<Object> s = (Serializer<Object>) Serializers.serializerFor(clazz);
				s.serialize(out, ret);
			}
			out.writeBoolean(false);
			out.flush();
		}
	}

	@SuppressWarnings("unchecked")
	public T async() {
		if (asyncFacade == null) {
			asyncFacade = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Object.class}, new InvocationHandler() {
				
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					request(method, args);
					return null;
				}
			});
		}
		return asyncFacade;
	}

	@SuppressWarnings("unchecked")
	public <R> T async(final Callback<R> callback) {
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Request r = request(method, args);
				if (!r.hasValue() && getFeeder() != null) {
					feeder.run();
				}
				r.addCallback(callback);
				if (method.getReturnType().isPrimitive()) {
					return 0;
				}
				return null;
			}
		});
	}

	public void setFeeder(Runnable feeder) {
		this.feeder = feeder;
	}

	public Runnable getFeeder() {
		return feeder;
	}

}
