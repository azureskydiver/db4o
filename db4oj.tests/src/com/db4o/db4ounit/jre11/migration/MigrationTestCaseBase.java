/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre11.migration;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public abstract class MigrationTestCaseBase implements TestCase, TestLifeCycle, OptOutNoFileSystemData {
	
	private static final String NULL_NAME = "NULL";
	
	private static final String MIN_VALUE_NAME = "MIN_VALUE";
	
	private static final String MAX_VALUE_NAME = "MAX_VALUE";
	
	private static final String ORDINARY_NAME = "REGULAR";	
	
	private ObjectContainer _container;
	
	private String _databaseFile;

	public void setUp() throws Exception {
		Db4o.configure().allowVersionUpdates(true);
		Db4o.configure().exceptionsOnNotStorable(false);
		prepareDatabaseFile();
		open();
	}
	
	protected ObjectContainer db() {
		return _container;
	}

	private void prepareDatabaseFile() throws IOException {
		_databaseFile = IOServices.buildTempPath(getDatabaseFileName());
		File4.copy(WorkspaceServices.workspaceTestFilePath("migration/" + getDatabaseFileName()), _databaseFile);
	}

	protected void reopen() {
		close();
		open();
	}

	private void open() {
		_container = Db4o.openFile(_databaseFile);
	}
	
	private void close() {
		if (null != _container) {
			_container.close();
			_container = null;
		}
	}

	public void tearDown() throws Exception {
		close();
		Db4o.configure().allowVersionUpdates(false);
	}

	protected MigrationItem getItem(String itemName) {
		final Query q = db().query();
		q.constrain(MigrationItem.class);
		q.descend("name").constrain(itemName);
		return (MigrationItem)q.execute().next();
	}

	protected void updateItemDate(String itemName, Object newValue) {
		final MigrationItem item = getItem(itemName);
		item.setValue(newValue);
		db().store(item);
	}

	protected void assertItem(final Object expectedValue, final String itemName) {
		Assert.areEqual(expectedValue, getItemValue(itemName), itemName);
	}

	private Object getItemValue(String itemName) {
		return getItem(itemName).getValue();
	}
	
	public void testValuesAreReadCorrectly() {
		assertItem(getOrdinaryValue(), ORDINARY_NAME);
		assertItem(getMaxValue(), MAX_VALUE_NAME);
		assertItem(getMinValue(), MIN_VALUE_NAME);
		assertItem(null, NULL_NAME);
	}
	
	public void testValueCanBeUpdated() {
		final Object updateValue = getUpdateValue();
		updateItemDate(NULL_NAME, getOrdinaryValue());
		updateItemDate(ORDINARY_NAME, updateValue);
		updateItemDate(MAX_VALUE_NAME, null);
		
		for (int i=0; i<2; ++i) {
			assertItem(null, MAX_VALUE_NAME);
			assertItem(getOrdinaryValue(), NULL_NAME);
			assertItem(updateValue, ORDINARY_NAME);
			reopen();
		}
	}
	
	public void generateFile() {
		new java.io.File(getDatabaseFileName()).delete();
		final ObjectContainer container = Db4o.openFile(getDatabaseFileName());
		try {
			container.store(newItem(NULL_NAME, null));
			container.store(newItem(MAX_VALUE_NAME, getMaxValue()));
			container.store(newItem(MIN_VALUE_NAME, getMinValue()));
			container.store(newItem(ORDINARY_NAME, getOrdinaryValue()));
		} finally {
			container.close();
		}
	}
	
	protected abstract MigrationItem newItem(String name, Object value);

	protected abstract String getDatabaseFileName();

	protected abstract Object getMinValue();

	protected abstract Object getMaxValue();

	protected abstract Object getOrdinaryValue();
	
	protected abstract Object getUpdateValue();

}
