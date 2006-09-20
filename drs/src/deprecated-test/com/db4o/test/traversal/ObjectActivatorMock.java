package com.db4o.test.traversal;

import com.db4o.drs.inside.traversal.ObjectActivator;

class ObjectActivatorMock implements ObjectActivator {

	int _activationCount = 0;

	public void activate(Object object) {
		_activationCount++;
	}

}
