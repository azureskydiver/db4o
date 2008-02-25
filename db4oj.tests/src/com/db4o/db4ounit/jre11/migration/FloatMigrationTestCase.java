/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.ConsoleTestRunner;

public class FloatMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Float value;
		
		public Item() {
		}
		
		public Item(String name_, Float value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Float) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Float)value);
	}
	
	protected String getDatabaseFileName() {
		return "floats.db4o";
	}
	
	protected Object getMinValue() {
		return new Float(Float.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Float(Float.MAX_VALUE-1);
	}

	protected Object getOrdinaryValue() {
		return new Float(41.9999);
	}
	
	protected Object getUpdateValue() {
		return new Float(Float.NaN);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
//		new FloatMigrationTestCase().generateFile();
		new ConsoleTestRunner(IntegerMigrationTestCase.class).run();
	}
}
