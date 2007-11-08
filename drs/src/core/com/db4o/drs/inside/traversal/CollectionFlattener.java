package com.db4o.drs.inside.traversal;

public interface CollectionFlattener extends CollectionHandler {

	boolean canHandle(Object obj);

	boolean canHandle(Class c);	
}
