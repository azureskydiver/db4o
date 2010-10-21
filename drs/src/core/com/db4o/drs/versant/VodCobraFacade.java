package com.db4o.drs.versant;

import java.util.*;

import com.db4o.qlin.*;
import com.versant.odbms.query.*;

public interface VodCobraFacade {

	void close();

	VodId idFor(long loid);

	long store(Object obj);
	
	void create(long loid, Object obj);

	void store(long loid, Object obj);

	Collection<Long> loids(Class<?> extent);

	<T> Collection<T> query(Class<T> extent);

	<T> Collection<T> readObjects(Class<T> extent, Object[] loids);

	<T> Collection<T> readObjects(Class<T> extent, Object[] loids, int limit);

	<T> T objectByLoid(long loid);
	
	boolean containsLoid(long loid);

	void commit();

	void rollback();

	<T> T singleInstanceOrDefault(Class<T> extent, T defaultValue);

	<T> T singleInstance(Class<T> extent);

	<T> QLin<T> from(Class<T> clazz);

	Object[] executeQuery(DatastoreQuery query);

	short databaseId();

	String databaseName();

	void delete(long loid);
	
	void deleteAll();
	
	String schemaName(Class clazz);
	
	boolean isKnownClass(Class clazz);
	
}
