package com.db4o.cs.server;

import com.db4o.ObjectContainer;
import com.db4o.cs.server.protocol.protocol1.Protocol1;
import com.db4o.cs.server.protocol.Protocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * This is specific to a single client connection.
 * 
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:50:06 AM
 */
public interface Session {
	/**
	 *
	 * @return
	 */
	ObjectContainer getObjectContainer();

	Object handle(byte operation, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException;

	void setProtocol(Protocol protocol);

	boolean close();

	boolean isClosed();
}
