/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.regression;

import java.util.Date;

import com.db4o.*;
import com.db4o.db4ounit.util.WorkspaceServices;
import com.db4o.foundation.io.*;
import com.db4o.query.Query;

import db4ounit.*;

public class COR312TestCase implements TestCase, TestLifeCycle {
	
	public static class Item {
		public String name;
		public Date date;
		
		public Item() {
		}
		
		public Item(String name_, Date date_) {
			name = name_;
			date = date_;
		}
	}
	
	private static final String NULL = "NULL";
	
	private static final String MAX_VALUE = "MAX_VALUE";
	
	private static final Date MAX_VALUE_DATE = new Date(Long.MAX_VALUE);
	
	private static final String REGULAR = "REGULAR";
	
	private static final Date REGULAR_DATE = new Date(106, 11, 23);
	
	public void _testDateFieldValues() {
		assertDateField(REGULAR_DATE, REGULAR);
		assertDateField(MAX_VALUE_DATE, MAX_VALUE);
		assertDateField(null, NULL);
	}

	private void assertDateField(final Date expectedValue, final String itemName) {
		Assert.areEqual(expectedValue, getItemDate(itemName), itemName);
	}
	
	private Date getItemDate(String itemName) {
		return getItem(itemName).date;
	}

	private Item getItem(String itemName) {
		final Query q = _container.query();
		q.constrain(Item.class);
		q.descend("name").constrain(itemName);
		return (Item)q.execute().next();
	}

	private ObjectContainer _container;
	
	public void setUp() throws Exception {
		String fname = Path4.buildTempPath("cor312.yap");
		File4.copy(WorkspaceServices.workspacePath("db4oj.tests/test/regression/cor312.yap"), fname);
		Db4o.configure().allowVersionUpdates(true);
		_container = Db4o.openFile(fname);
	}

	public void tearDown() throws Exception {
		if (null != _container) {
			_container.close();
			_container = null;
		}
	}
	
	public static void main(String[] args) {
		new TestRunner(COR312TestCase.class).run();
		// reference db4o 5.2 and call generateFile if
		// you ever need to regenerate the file again
		// generateFile();
	}	

//	private static void generateFile() {
//		final String fname = "cor312.yap";
//		new java.io.File(fname).delete();
//		final ObjectContainer container = Db4o.openFile(fname);
//		try {
//			container.set(new Item(NULL, null));
//			container.set(new Item(MAX_VALUE, MAX_VALUE_DATE));
//			container.set(new Item(REGULAR, REGULAR_DATE));
//		} finally {
//			container.close();
//		}
//	}

}
