/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.internal.query.processor.*;


class Order implements Orderable {
	
	private int i_major;
	private int i_minor;
	
	public int compareTo(Object obj) {
		if(obj instanceof Order){
			Order other = (Order)obj;
			int res = other.i_major - i_major;
			if(res != 0){
				return res;
			}
			return other.i_minor - i_minor;
		}
		return 1;
	}

	public void hintOrder(int a_order, boolean a_major) {
		if(a_major){
			i_major = a_order;
		}else{
			i_minor = a_order;
		}
	}
	
	public boolean hasDuplicates(){
		return true;
	}
}

