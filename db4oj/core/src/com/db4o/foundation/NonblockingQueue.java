/* Copyright (C) 2004 -2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;


/**
 * Using the CollectionElement the other way around:
 * CollectionElement.i_next points to the previous element
 * 
 * @exclude
 */
public class NonblockingQueue implements Queue4 {
    
	protected final class Queue4Iterator implements Iterator4 {
	    
		protected boolean _active=false;
		protected List4 _current=null;
		
		public Object current() {
			return _current._element;
		}

		public boolean moveNext() {
			if(!_active) {
				_current=_last;
				_active=true;
			}
			else {
				if(_current!=null) {
					_current=_current._next;
				}
			}
			return _current!=null;
		}

		public void reset() {
			_current=null;
			_active=false;
		}
	}

	private List4 _first;
	protected List4 _last;
	
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#add(java.lang.Object)
	 */
    public final void add(Object obj) {
    	List4 ce = new List4(null, obj); 
    	if(_first == null){
    		_last = ce;
    	}else{
    		_first._next = ce;
    	}
    	_first = ce;
    }
    
	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#next()
	 */
	public final Object next() {
		if(_last == null){
			return null;
		}
		Object ret = _last._element;
		_last = _last._next;
		if(_last == null){
			_first = null;
		}
		return ret;
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#hasNext()
	 */
    public final boolean hasNext(){
        return _last != null;
    }

	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#iterator()
	 */
	public Iterator4 iterator() {
		return new Queue4Iterator();
	}
    
}
