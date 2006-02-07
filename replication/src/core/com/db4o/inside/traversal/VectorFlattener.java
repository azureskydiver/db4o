package com.db4o.inside.traversal;

import java.util.Vector;

import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;

public class VectorFlattener implements CollectionFlattener {

	public boolean canHandle(ReflectClass claxx) {
		String name = claxx.getName();
		return name.equals(Vector.class.getName());
	}

	public boolean canHandle(Object obj) {
		String name = obj.getClass().getName();
		return name.equals(Vector.class.getName());
	}

	public Iterator4 iteratorFor(final Object collection) {
		Vector vector = (Vector)collection;
		Collection4 collection4 = new Collection4();

		for (int i = 0; i < vector.size(); i++) {
			collection4.add(vector.elementAt(i));
		}

		return collection4.iterator();
	}

}
