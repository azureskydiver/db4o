package com.db4o.drs.inside.traversal;

import com.db4o.internal.traversal.*;

public interface CollectionFlattener extends CollectionHandler {

	boolean canHandle(Object obj);

	boolean canHandle(Class c);	
}
