package com.db4o.inside.traversal;

import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;

public interface CollectionFlattener {

	boolean canHandle(ReflectClass claxx);

	boolean canHandle(Object obj);

	boolean canHandle(Class c);

	Iterator4 iteratorFor(Object collection);
}
