package com.db4o.cs.server;

import com.db4o.ObjectContainer;
import com.db4o.cs.server.protocol.Protocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:50:11 AM
 */
public class DefaultSession implements Session {
	private ObjectContainer oc;
	private Protocol protocol;
	private boolean closed;
	private Context context;

	public DefaultSession(Context context) {

		this.context = context;
	}

	public synchronized ObjectContainer getObjectContainer() {
		if(oc == null){
			oc = context.getObjectContainer();
		}
		return oc;
	}

	public Object handle(byte operation, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		return protocol.handle(operation, oin, oout);
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public boolean close() {
		getObjectContainer().close();
		closed = true;
		return closed;
	}

	public Context getContext() {
		return context;
	}

	public boolean isClosed(){
		return closed;
	}
}
