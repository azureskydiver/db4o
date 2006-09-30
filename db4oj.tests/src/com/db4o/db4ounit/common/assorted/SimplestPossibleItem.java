/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

public class SimplestPossibleItem {
    
    public String name;
    
    public SimplestPossibleItem() {
    }
    
    public SimplestPossibleItem(String name_) {
        this.name = name_;
    }

    public String getName() {
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}
}
