package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.ObjectContainer;
import com.db4o.query.Query;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:30:47 PM
 */
public class QueryOperationHandler implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		String className = (String) oin.readObject();
		System.out.println("getting objects for class: " + className);
		ObjectContainer oc = session.getObjectContainer(context);
		System.out.println("got oc");
		Query q = oc.query();
		System.out.println("got q");
		/*
		// what the heck?  this hangs db4o, never makes it to the next sys out
		q.constrain(className);
		System.out.println("cons");*/
		List results = q.execute();
		System.out.println("result size: " + results.size());
		oout.writeInt(results.size());
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			long objectId = oc.ext().getID(o);
			oout.writeLong(objectId);
			oout.writeObject(o);
		}
		oout.flush();
	}
}
