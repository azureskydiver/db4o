package com.db4o.foundation;

public class FixedObjectPool<T> implements ObjectPool<T> {
	
	static class PoolEntry<T> {
		public final T object;
		public boolean free;
		
		public PoolEntry(T o) {
			object = o;
			free = true;
        }
	};
	
	private final PoolEntry<T>[] _entries;

	public FixedObjectPool(T... pooledObjects) {
		_entries = new PoolEntry[pooledObjects.length];
		for (int i=0; i<_entries.length; ++i) {
			_entries[i] = new PoolEntry(pooledObjects[i]);
		}
    }

	public T borrowObject() {
		for (PoolEntry<T> entry : _entries) {
	        if (entry.free) {
	        	entry.free = false;
	        	return entry.object;
	        }
        }
		throw new IllegalStateException("pool has been exhausted");
    }

	public void returnObject(T o) {
		for (PoolEntry<T> entry : _entries) {
	        if (entry.object == o) {
	        	if (entry.free) {
	        		throw new IllegalStateException();
	        	}
	        	entry.free = true;
	        	return;
	        }
        }
		throw new IllegalArgumentException();
    }

}
