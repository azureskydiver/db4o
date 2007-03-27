package com.db4o.db4ounit.jre11.migration;

import db4ounit.TestRunner;

public class IntegerMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item implements MigrationItem {
		public String name;
		public Integer value;
		
		public Item() {
		}
		
		public Item(String name_, Integer value_) {
			name = name_;
			value = value_;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value_) {
			value = (Integer) value_;
		}
	}
	
	protected String getDatabaseFileName() {
		return "integers.db4o";
	}
	
	protected Object getMinValue() {
		return new Integer(0);
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
		new TestRunner(IntegerMigrationTestCase.class).run();
		// reference db4o 5.2 and call generateFile if
		// you ever need to regenerate the file again
//		 generateFile();
	}


//	private static void generateFile() {
//		final String fname = "integers.db4o";
//		new java.io.File(fname).delete();
//		final ObjectContainer container = Db4o.openFile(fname);
//		try {
//			container.set(new Item(NULL, null));
//			container.set(new Item(MAX_VALUE_NAME, MAX_VALUE));
//			container.set(new Item(MIN_VALUE_NAME, MIN_VALUE));
//			container.set(new Item(ORDINARY_NAME, ORDINARY));
//		} finally {
//			container.close();
//		}
//	}


	

}
