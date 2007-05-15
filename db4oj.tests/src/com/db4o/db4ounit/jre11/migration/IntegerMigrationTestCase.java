﻿/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.TestRunner;

public class IntegerMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Integer value;
		
		public Item() {
		}
		
		public Item(String name_, Integer value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Integer) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Integer)value);
	}
	
	protected String getDatabaseFileName() {
		return "integers.db4o";
	}
	
	protected Object getMinValue() {
		return new Integer(Integer.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Integer(Integer.MAX_VALUE-1);
	}

	protected Object getOrdinaryValue() {
		return new Integer(42);
	}
	
	protected Object getUpdateValue() {
		return new Integer(360);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new IntegerMigrationTestCase().generateFile();
		new TestRunner(IntegerMigrationTestCase.class).run();
	}
}
