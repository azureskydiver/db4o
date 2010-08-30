package com.db4o.rmi;


public class PeerServer {

	private final long id;
	private final Object object;

	public PeerServer(long id, Object object) {
		this.id = id;
		this.object = object;
	}

	public long getId() {
		return id;
	}

	public Object getObject() {
		return object;
	}

}
