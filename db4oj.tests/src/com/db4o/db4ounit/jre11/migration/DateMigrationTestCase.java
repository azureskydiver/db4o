/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.migration;

import java.util.Date;

import db4ounit.TestRunner;

public class DateMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item implements MigrationItem {
		public String name;
		public Date date;
		
		public Item() {
		}
		
		public Item(String name_, Date date_) {
			name = name_;
			date = date_;
		}
		
		public Object getValue() {
			return date;
		}
		
		public void setValue(Object value) {
			date = (Date)value;
		}
	}

	protected Object getMinValue() {
		return new Date(0);
	}

	protected Object getMaxValue() {
		return new Date(Long.MAX_VALUE - 1);
	}

	protected Object getOrdinaryValue() {
		return new Date(1166839200000L);
	}
	
	protected Object getUpdateValue() {
		return new Date(28, 5, 14);
	}

	protected String getDatabaseFileName() {
		return "dates.db4o";
	}
	
	public static void main(String[] args) {
		new TestRunner(DateMigrationTestCase.class).run();
		// reference db4o 5.2 and call generateFile if
		// you ever need to regenerate the file again
		// generateFile();
	}


//	private static void generateFile() {
//		final String fname = "dates.db4o";
//		new java.io.File(fname).delete();
//		final ObjectContainer container = Db4o.openFile(fname);
//		try {
//			container.set(new Item(NULL, null));
//			container.set(new Item(MAX_VALUE, MAX_VALUE_DATE));
//			container.set(new Item(MIN_VALUE, MIN_VALUE_DATE));
//			container.set(new Item(REGULAR, REGULAR_DATE));
//		} finally {
//			container.close();
//		}
//	}

}
