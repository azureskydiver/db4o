/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;
import com.db4o.test.replication.ArrayHolder;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public abstract class DrsTestCase implements TestCase, TestLifeCycle {
	
	private DrsFixture _a;
	private DrsFixture _b;
	
	public void setUp() throws Exception {
		cleanBoth();
		configure();
		openBoth();
		store();
		reopen();
	}

	private void cleanBoth() {
		_a.clean();
		_b.clean();
	}
	
	protected void store() {}
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}
	
	protected void reopen() throws Exception {
		closeBoth();
		openBoth();
	}

	private void openBoth() throws Exception {
		_a.open();
		_b.open();
	}
	
	public void tearDown() throws Exception {
		closeBoth();
		cleanBoth();
	}

	private void closeBoth() throws Exception {
		_a.close();
		_b.close();
	}
	
	public void a(DrsFixture fixture) {
		_a = fixture;
	}
	
	public void b(DrsFixture fixture) {
		_b = fixture;
	}
	
	public DrsFixture a() {
		return _a;
	}

	public DrsFixture b() {
		return _b;
	}



}
