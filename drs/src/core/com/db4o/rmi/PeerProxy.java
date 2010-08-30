package com.db4o.rmi;

import java.lang.reflect.*;

public class PeerProxy<T> implements Peer<T> {

	private T syncFacade;

	private T asyncFacade;

	private Class<T> rootFacade;

	private final Distributor<?> distributor;
	private final long id;

	public PeerProxy(Distributor<?> distributor, long id, Class<T> rootFacade) {
		this.id = id;
		this.rootFacade = rootFacade;
		this.distributor = distributor;
	}

	public T sync() {
		if (syncFacade == null) {
			syncFacade = (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { rootFacade }, new InvocationHandler() {

				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Request r = distributor.request(getId(), method, args, true);
					if (!r.hasValue()) {
						distributor.feed();
					}
					return r.get();
				}
			});
		}
		return syncFacade;
	}

	public T async() {
		if (asyncFacade == null) {
			asyncFacade = async(null);
		}
		return asyncFacade;
	}

	public <R> T async(final Callback<R> callback) {
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { rootFacade }, new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Request r = distributor.request(getId(), method, args, callback != null);
				if (callback != null) {
					if (!r.hasValue()) {
						distributor.feed();
					}
					r.addCallback(callback);
				}
				if (method.getReturnType().isPrimitive()) {
					return 0;
				}
				return null;
			}
		});
	}

	public long getId() {
		return id;
	}

}
