/* Copyright (C) 2007   Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

public abstract class MigrationItem {

	public String name;
	
	public MigrationItem() {
	}
	
	public MigrationItem(String name_) {
		name = name_;
	}
	
	public abstract Object getValue();
	public abstract void setValue(Object value_);
}
