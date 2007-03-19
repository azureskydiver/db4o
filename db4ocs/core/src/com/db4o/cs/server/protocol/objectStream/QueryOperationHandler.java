package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.cs.common.util.Log;
import com.db4o.cs.common.util.StopWatch;
import com.db4o.cs.common.query.QueryConverter;
import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import com.db4o.query.Constraint;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:30:47 PM
 */
public class QueryOperationHandler implements OperationHandler {
    public static StopWatch stopWatchQuery = new StopWatch(); // NOTE: this is only good for single threaded tests

    public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		ClientQuery query = (ClientQuery) oin.readObject();
		//System.out.println("getting objects for query: " + query);
        stopWatchQuery.start();
        ObjectContainer oc = session.getObjectContainer();
		Query q = oc.query();
		// now apply all the constraints
		QueryConverter.applyConstraints(q, query);
		/*
		// what the heck?  this hangs db4o, never makes it to the next sys out
		q.constrain(className);
		System.out.println("cons");*/
		List results = q.execute();
        stopWatchQuery.stop();
        Log.print("result size: " + results.size());
		oout.writeInt(results.size());
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			long objectId = oc.ext().getID(o);
			oout.writeLong(objectId);
			// sometimes the object is null???  probably deleted before the next thread gets here.... interesting problem
			//System.out.println("sending back: " + o);
			oout.writeObject(o);
		}
		oout.flush();
		return results.size();
	}


}
