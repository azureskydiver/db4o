package com.db4o.inside.traversal;

import com.db4o.foundation.Iterator4;
import com.db4o.reflect.ReflectClass;

public interface CollectionFlattener {

	boolean canHandle(ReflectClass claxx);

	Iterator4 iteratorFor(Object collection);

}
