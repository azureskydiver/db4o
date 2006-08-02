/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * @exclude
 */
class QResultClient extends QueryResultImpl {

	private Object[] i_prefetched = new Object[YapConst.PREFETCH_OBJECT_COUNT];
	private int i_remainingObjects;
	private int i_prefetchCount = YapConst.PREFETCH_OBJECT_COUNT;
	private int i_prefetchRight;

	QResultClient(Transaction a_ta) {
		super(a_ta);
	}
    
    QResultClient(Transaction a_ta, int initialSize) {
        super(a_ta, initialSize);
    }

	
	public boolean hasNext() {
		synchronized (streamLock()) {
			if(i_remainingObjects > 0){
				return true;
			}
			return super.hasNext();
		}
	}
	

	public Object next() {
		synchronized (streamLock()) {
			YapClient stream = (YapClient)i_trans.stream();
			stream.checkClosed();
			if (i_remainingObjects < 1) {
				if (super.hasNext()) {
					i_remainingObjects = (stream).prefetchObjects(this, i_prefetched, i_prefetchCount);
					i_prefetchRight=i_remainingObjects;
				}
			}
			i_remainingObjects --;
			if(i_remainingObjects < 0){
				return null;
			}
			if(i_prefetched[i_prefetchRight-i_remainingObjects-1] == null){
				return next();
			}
			return activate(i_prefetched[i_prefetchRight-i_remainingObjects-1]);
		}
	}
	
	public void reset() {
		synchronized (streamLock()) {
			i_remainingObjects = 0;
			i_prefetchRight=0;
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


}
