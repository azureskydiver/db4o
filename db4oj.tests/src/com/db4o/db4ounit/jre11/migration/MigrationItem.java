package com.db4o.db4ounit.jre11.migration;

public interface MigrationItem {
	Object getValue();
	void setValue(Object value_);
}
