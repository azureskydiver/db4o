/* Copyright (C) 2004 -2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class NonblockingQueue implements Queue4 {

	private List4 _insertionPoint;
	private List4 _next;
	
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#add(java.lang.Object)
	 */
    public final void add(Object obj) {
    	List4 newNode = new List4(null, obj);
    	if (_insertionPoint == null) {
    		_next = newNode;
    	} else {
    		_insertionPoint._next = newNode;
    	}
    	_insertionPoint = newNode;
    }
    
	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#next()
	 */
	public final Object next() {
		if(_next == null){
			return null;
		}
		Object ret = _next._element;
		removeNext();
		return ret;
	}

	private void removeNext() {
		_next = _next._next;
		if (_next == null) {
			_insertionPoint = null;
		}
	}
	
	public Object nextMatching(Predicate4 condition) {
		if (null == condition) {
			throw new ArgumentNullException();
		}
		
		List4 current = _next;
		List4 previous = null;
		while (null != current) {
			final Object element = current._element;
			if (condition.match(element)) {
				if (previous == null) {
					removeNext();
				} else {
					previous._next = current._next;
				}
				return element;
			}
			previous = current;
			current = current._next;
		}
		return null;
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#hasNext()
	 */
    public final boolean hasNext(){
        return _next != null;
    }

	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#iterator()
	 */
	public Iterator4 iterator() {
		return new Iterator4Impl(_next);
	}

}
