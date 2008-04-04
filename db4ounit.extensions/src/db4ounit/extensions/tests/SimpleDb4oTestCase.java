/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.tests;

import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SimpleDb4oTestCase extends AbstractDb4oTestCase {
	
	public static final DynamicVariable EXPECTED_FIXTURE_VARIABLE = new DynamicVariable();
	
	public static class Data {}
	
	private boolean[] _everythingCalled=new boolean[3];
	
	protected void configure(Configuration config) {
		Assert.areSame(expectedFixture(), fixture());
		Assert.isTrue(everythingCalledBefore(0));
		_everythingCalled[0]=true;
	}

	private Db4oFixture expectedFixture() {
		return (Db4oFixture) EXPECTED_FIXTURE_VARIABLE.value();
	}
	
	protected void store() {
		Assert.isTrue(everythingCalledBefore(1));
		_everythingCalled[1]=true;
		fixture().db().store(new Data());
	}
	
	public void testResultSize() {
		Assert.isTrue(everythingCalledBefore(2));
		_everythingCalled[2] = true;
		Assert.areEqual(1, fixture().db().queryByExample(Data.class).size());
	}
	
	public boolean everythingCalled() {
		return everythingCalledBefore(_everythingCalled.length);
	}

	public boolean everythingCalledBefore(int idx) {
		for (int i = 0; i < idx; i++) {
			if(!_everythingCalled[i]) {
				return false;
			}
		}
		for (int i = idx; i < _everythingCalled.length; i++) {
			if(_everythingCalled[i]) {
				return false;
			}
		}
		return true;
	}
}
