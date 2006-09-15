/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
class QResultClient extends QueryResultImpl {

	private Object[] _prefetchedObjects;
	private int _remainingObjects;
	private int _prefetchRight;

	QResultClient(Transaction ta) {
		super(ta);
	}
    
    QResultClient(Transaction ta, int initialSize) {
        super(ta, initialSize);
    }

	
	public boolean hasNext() {
		synchronized (streamLock()) {
			if(_remainingObjects > 0){
				return true;
			}
			return super.hasNext();
		}
	}
	

	public Object next() {
		synchronized (streamLock()) {
			YapClient stream = (YapClient)i_trans.stream();
			stream.checkClosed();
			int prefetchCount=stream.config().prefetchObjectCount();
			ensureObjectCacheAllocated(prefetchCount);
			if (_remainingObjects < 1) {
				if (super.hasNext()) {
					_remainingObjects = (stream).prefetchObjects(this, _prefetchedObjects, prefetchCount);
					_prefetchRight=_remainingObjects;
				}
			}
			_remainingObjects --;
			if(_remainingObjects < 0){
				return null;
			}
			if(_prefetchedObjects[_prefetchRight-_remainingObjects-1] == null){
				return next();
			}
			return activate(_prefetchedObjects[_prefetchRight-_remainingObjects-1]);
		}
	}
	
	public void reset() {
		synchronized (streamLock()) {
			_remainingObjects = 0;
			_prefetchRight=0;
			super.reset();
		}
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
