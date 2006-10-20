/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.cs;

import com.db4o.foundation.*;

/**
 * @exclude
 */
class QResultClientIterator implements Iterator4 {
	
	private Object[] _prefetchedObjects;
	private int _remainingObjects;
	private int _prefetchRight;
	private final QResultClient _client;
	private final IntIterator4 _ids;
	
	public QResultClientIterator(QResultClient client) {
		_client = client;
		_ids = client.iterateIDs();
	}

	public Object current() {
		synchronized (streamLock()) {
			return _client.activate(prefetchedCurrent());
		}
	}

	private Object streamLock() {
		return _client.streamLock();
	}
	
	public void reset() {
		_remainingObjects = 0;
		_ids.reset();
	}

	public boolean moveNext() {
		synchronized (streamLock()) {
			if (_remainingObjects > 0) {
				--_remainingObjects;
				return skipNulls();
			}
			
			prefetch();
			
			--_remainingObjects;
			if(_remainingObjects < 0){
				return false;
			}
			return skipNulls();
		}
	}

	private boolean skipNulls() {
		// skip nulls (deleted objects)
		if (prefetchedCurrent() == null){
			return moveNext();
		}
		return true;
	}

	private void prefetch() {
		ensureObjectCacheAllocated(prefetchCount());
		_remainingObjects = stream().prefetchObjects(_ids, _prefetchedObjects, prefetchCount());
		_prefetchRight=_remainingObjects;
	}

	private int prefetchCount() {
		return stream().config().prefetchObjectCount();
	}

	private YapClient stream() {
		return (YapClient) _client.stream();
	}

	private Object prefetchedCurrent() {
		return _prefetchedObjects[_prefetchRight-_remainingObjects-1];
	}
	
	// TODO: open this as an external tuning interface in ExtObjectSet
	
//	public void prefetch(int count){
//		if(count < 1){
//			count = 1;
//		}
//		i_prefetchCount = count;
//		Object[] temp = new Object[i_prefetchCount];
//		if(i_remainingObjects > 0){
//			// Potential problem here: 
//			// On reducing the prefetch size, this will crash.
//			System.arraycopy(i_prefetched, 0, temp, 0, i_remainingObjects);
//		}
//		i_prefetched = temp;
//	}

	private void ensureObjectCacheAllocated(int prefetchObjectCount) {
		if(_prefetchedObjects==null) {
			_prefetchedObjects = new Object[prefetchObjectCount];
			return;
		}
		if(prefetchObjectCount>_prefetchedObjects.length) {
			Object[] newPrefetchedObjects=new Object[prefetchObjectCount];
			System.arraycopy(_prefetchedObjects, 0, newPrefetchedObjects, 0, _prefetchedObjects.length);
			_prefetchedObjects=newPrefetchedObjects;
		}
	}


}
