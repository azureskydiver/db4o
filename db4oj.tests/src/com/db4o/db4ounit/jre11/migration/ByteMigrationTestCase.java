/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import db4ounit.*;

public class ByteMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Byte value;
		
		public Item() {
		}
		
		public Item(String name_, Byte value_) {
			super(name_);
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Byte) value_;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Byte)value);
	}
	
	protected String getDatabaseFileName() {
		return "bytes.db4o";
	}
	
	protected Object getMinValue() {
		return new Byte(Byte.MIN_VALUE);
	}

	protected Object getMaxValue() {
		return new Byte((byte)(Byte.MAX_VALUE-1));
	}

	protected Object getOrdinaryValue() {
		return new Byte((byte)42);
	}
	
	protected Object getUpdateValue() {
		return new Byte((byte)33);
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new ByteMigrationTestCase().generateFile();
		new ConsoleTestRunner(ByteMigrationTestCase.class).run();
	}
}
