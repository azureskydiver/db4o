package com.db4o.cs.client;

import com.db4o.cs.common.ClassMetaData;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 12:34:08 PM
 */
public interface ClientContext {
	ClassMetaData getClassMetaData(String className);

	void setClassMetaData(String className, ClassMetaData classMetaData);

	Long getIdForObject(Object o);
}
