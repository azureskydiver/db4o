/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.io.File;

import junit.framework.TestCase;

public class DatabaseGraphIteratorTest extends TestCase {
	
	private Database database;

	protected void setUp() throws Exception {
		super.setUp();
		File databaseFile = new File("test.yap");
		databaseFile.delete();
		
		database = new Database();
		database.open("test.yap");
		storeSomeObjects();
	}

	private void storeSomeObjects() {
		Employee root = new Employee("Typhoid Mary", new Employee[] {
				new Employee("John Doe"),
				new Employee("George Vancouver", new Employee[] {
						new Employee("Frank"),
						new Employee("Joe"),
						new Employee("Chet")
				}),
				new Employee("Laurel"),
				new Employee("Frank")
		});
		
		database.container.set(root);
		database.container.commit();
	}
	
	private class Employee {
		private String name;
		private Employee[] subordinates;

		public Employee(String name) {
			this.name = name;
		}
		
		public Employee(String name, Employee[] reportsTo) {
			this.name = name;
			this.subordinates = reportsTo;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		database.close();
	}

	public void testGetPath() {
	}

	public void testSetPath() {
	}

}
