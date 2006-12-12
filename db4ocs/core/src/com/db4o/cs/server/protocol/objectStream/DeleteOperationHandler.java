package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.common.util.StopWatch;
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
	public static StopWatch stopWatchInsantiation = new StopWatch(); // NOTE: this is only good for single threaded tests

	public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		long objectId = oin.readLong();
		stopWatchInsantiation.start();
		ObjectContainer oc = session.getObjectContainer();
		Object o = getObjectById(oc, objectId);
		oc.delete(o);
		stopWatchInsantiation.stop();
		return 1; // could count up all the objects that were actually deleted
	}

	private Object getObjectById(ObjectContainer oc, long objectId) {
		return oc.ext().getByID(objectId);
	}
}
