/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

class Null implements YapComparable{
    
    public int compareTo(Object a_obj) {
        if(a_obj == null) {
            return 0;
        }
        return -1;
    }
    
	public boolean equals(Object obj){
		return obj == null;
	}
	
	public boolean isEqual(Object obj) {
		return obj == null;
	}

	public boolean isGreater(Object obj) {
		return false;
	}

	public boolean isSmaller(Object obj) {
		return false;
	}

	public YapComparable prepareComparison(Object obj) {
		// do nothing
		return this;
	}
	
	static final YapComparable INSTANCE = new Null();

}

