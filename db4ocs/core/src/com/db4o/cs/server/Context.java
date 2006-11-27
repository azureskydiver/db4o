package com.db4o.cs.server;

import com.db4o.ObjectContainer;
import com.db4o.cs.common.ClassMetaData;

import java.util.Map;

/**
 * This is the global context for the server.
 * 
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:33:29 AM
 */
public interface Context {

	Map getAccessMap();

	int getClientId();

	ObjectContainer getObjectContainer();

	void addClassMetaData(ClassMetaData classMetaData);

	ClassMetaData getClassMetaData(String className);
}
