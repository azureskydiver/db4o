package com.db4o.foundation;

public interface KeyValueIterator {

	public boolean moveNext();

	public Object key();

	public Object value();
}