/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

public interface ObjectPool<T> {

	T borrowObject();

	void returnObject(T o);

}
