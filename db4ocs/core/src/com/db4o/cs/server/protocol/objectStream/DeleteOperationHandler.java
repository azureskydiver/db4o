package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.ObjectContainer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:54:28 AM
 */
public class DeleteOperationHandler implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		long objectId = oin.readLong();
		ObjectContainer oc = session.getObjectContainer(context);
		getObjectById(objectId);
		//oc.delete();
	}

	private void getObjectById(long objectId) {
		
	}
}
