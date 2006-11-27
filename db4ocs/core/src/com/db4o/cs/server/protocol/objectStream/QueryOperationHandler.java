package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.ObjectContainer;
import com.db4o.reflect.ReflectClass;
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
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		ClientQuery query = (ClientQuery) oin.readObject();
		//System.out.println("getting objects for query: " + query);
		ObjectContainer oc = session.getObjectContainer(context);
		Query q = oc.query();
		// now apply all the constraints
		applyConstraints(oc, q, query);
		/*
		// what the heck?  this hangs db4o, never makes it to the next sys out
		q.constrain(className);
		System.out.println("cons");*/
		List results = q.execute();
		//System.out.println("result size: " + results.size());
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
	}

	private void applyConstraints(ObjectContainer oc, Query q, ClientQuery query) {
		List<Constraint> constraints = query.getConstraints();
		for (int i = 0; i < constraints.size(); i++) {
			Constraint constraint = constraints.get(i);
			//System.out.println("applying constraint: " + constraint.getObject());
			q.constrain(constraint.getObject());
			/*
			// todo: use this when not using Serialization for constrain by class
			ReflectClass rc = oc.ext().reflector().forName(classname);
			if(rc == null){
				// todo: should send error back to client.
			} else {
				q.constrain()
			}*/
		}
		// now recurse through children
		Map<String, ClientQuery> children = query.getChildren();
		Iterator iter = children.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			System.out.println("descending into: " + key);
			ClientQuery qc2 = children.get(key);
			Query q2 = q.descend(key);
			applyConstraints(oc, q2, qc2);
		}

	}
}
