/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * Using the CollectionElement the other way around:
 * CollectionElement.i_next points to the previous element
 */
class Queue4 {
	private List4 i_first;
	private List4 i_last;
	
	
    final void add(Object obj){
    	List4 ce = new List4(null, obj); 
    	if(i_first == null){
    		i_last = ce;
    	}else{
    		i_first.i_next = ce;
    	}
    	i_first = ce;
    }
    
	final Object next(){
		if(i_last == null){
			return null;
		}
		Object ret = i_last.i_object;
		i_last = i_last.i_next;
		if(i_last == null){
			i_first = null;
		}
		return ret;
	}
	
	
}
