package com.db4o.test.unit.test;

import com.db4o.test.unit.db4o.*;

public class SimpleDb4oTestCase extends Db4oTestCase {
	private static class Data {}
	
	private boolean[] _everythingCalled=new boolean[3];
	
	protected void configure() {
		assertTrue(everythingCalledBefore(0));
		_everythingCalled[0]=true;
	}
	
	protected void store() {
		assertTrue(everythingCalledBefore(1));
		_everythingCalled[1]=true;
		fixture().db().set(new Data());
	}
	
	public void testResultSize() {
		assertTrue(everythingCalledBefore(2));
		_everythingCalled[2]=true;
		assertEquals(1,fixture().db().query(Data.class).size());
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
