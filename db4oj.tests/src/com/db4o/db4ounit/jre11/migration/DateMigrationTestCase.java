/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.migration;

import java.util.*;

import db4ounit.*;

public class DateMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Date date;
		
		public Item() {
		}
		
		public Item(String name_, Date date_) {
			super(name_);
			date = date_;
		}
		
		public Object getValue() {
			return date;
		}
		
		public void setValue(Object value) {
			date = (Date)value;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Date)value);
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
	    return new Date(1296839800000L);
	}

	protected String getDatabaseFileName() {
		return "dates.db4o";
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new DateMigrationTestCase().generateFile();

		new ConsoleTestRunner(DateMigrationTestCase.class).run();
	}
}
