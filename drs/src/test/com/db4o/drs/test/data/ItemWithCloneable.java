package com.db4o.drs.test.data;

public final class ItemWithCloneable {
	public Cloneable value;
	
	public ItemWithCloneable() {
	}
	
	public ItemWithCloneable(Cloneable c) {
		value = c;
	}
}