/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.ConsoleTestRunner;

public class LongMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Long value;
		
		public Item() {
		}
		
		public Item(String name_, Long value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Long) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Long)value);
	}
	
	protected String getDatabaseFileName() {
		return "longs.db4o";
	}
	
	protected Object getMinValue() {
		return new Long(Long.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Long(Long.MAX_VALUE-1);
	}

	protected Object getOrdinaryValue() {
		return new Long(42);
	}
	
	protected Object getUpdateValue() {
		return new Long(360);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new LongMigrationTestCase().generateFile();
		new ConsoleTestRunner(LongMigrationTestCase.class).run();
	}
}
