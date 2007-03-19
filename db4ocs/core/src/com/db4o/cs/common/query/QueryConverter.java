package com.db4o.cs.common.query;

import com.db4o.cs.client.query.ClientQuery;
import com.db4o.query.Query;
import com.db4o.query.Constraint;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * User: treeder
 * Date: Mar 15, 2007
 * Time: 6:38:52 PM
 */
public class QueryConverter {
	public static void applyConstraints(Query q, ClientQuery query) {
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
			applyConstraints(q2, qc2);
		}

	}
}
