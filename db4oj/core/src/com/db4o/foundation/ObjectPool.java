package com.db4o.foundation;

public interface ObjectPool<T> {

	T borrowObject();

	void returnObject(T o);

}
