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
