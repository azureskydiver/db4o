package com.db4o.drs.inside;

import com.db4o.drs.inside.traversal.CollectionFlattener;
import com.db4o.reflect.ReflectClass;

public interface CollectionHandler extends CollectionFlattener {

	Object emptyClone(CollectionSource sourceProvider, Object originalCollection, ReflectClass originalCollectionClass);

	void copyState(Object original, Object dest, CounterpartFinder finder);
	
	Object cloneWithCounterparts(CollectionSource sourceProvider, Object original, ReflectClass claxx, CounterpartFinder elementCloner);

}
