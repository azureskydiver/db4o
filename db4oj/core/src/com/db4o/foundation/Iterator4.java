/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o.foundation;


/**
 * @exclude
 */
public class Iterator4 implements IIterator4
{
    public static final IIterator4 EMPTY = new EmptyIterator();
    
	private List4 _next;

	public Iterator4(List4 first){
		_next = first;
	}

	public boolean hasNext(){
		return _next != null;
	}

	public Object next(){
		Object obj = _next._element;
		_next = _next._next;
		return obj;
	}
}
