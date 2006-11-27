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
 * Time: 1:14:51 AM
 */
public class SetOperationHandler extends ObjectOperationHandlerBase implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		long objectId = oin.readLong();
		Object o = oin.readObject();
		//System.out.println("setOp: " + o);
		//System.out.println("object received, id:" + objectId + " containing:" + o);
		ObjectContainer oc = session.getObjectContainer(context);
		if (objectId > 0) { // todo: check for a character first 'c' or 's' rather than the long, as in spec doc
			oc.ext().bind(o, objectId);
			oc.set(o);
		} else {
			oc.set(o);
		}
	}
}
