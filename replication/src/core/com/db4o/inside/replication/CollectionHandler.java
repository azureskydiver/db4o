package com.db4o.inside.replication;

import com.db4o.inside.traversal.CollectionFlattener;
import com.db4o.reflect.ReflectClass;

public interface CollectionHandler extends CollectionFlattener {

	Object emptyClone(Object originalCollection, ReflectClass originalCollectionClass);

	void copyState(Object original, Object dest, CounterpartFinder finder);

}
