/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o;

/**
 * @exclude
 */
public class Iterator4
{
	private List4 i_next;

	Iterator4(List4 a_first){
		i_next = a_first;
	}

	public boolean hasNext(){
		return i_next != null;
	}

	public Object next(){
		Object obj = i_next.i_object;
		i_next = i_next.i_next;
		return obj;
	}
}
