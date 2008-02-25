/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.ConsoleTestRunner;

public class ShortMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Short value;
		
		public Item() {
		}
		
		public Item(String name_, Short value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Short) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Short)value);
	}
	
	protected String getDatabaseFileName() {
		return "shorts.db4o";
	}
	
	protected Object getMinValue() {
		return new Short(Short.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Short((short)(Short.MAX_VALUE-1));
	}

	protected Object getOrdinaryValue() {
		return new Short((short)42);
	}
	
	protected Object getUpdateValue() {
		return new Short((short)360);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new ShortMigrationTestCase().generateFile();
		new ConsoleTestRunner(ShortMigrationTestCase.class).run();
	}
}
