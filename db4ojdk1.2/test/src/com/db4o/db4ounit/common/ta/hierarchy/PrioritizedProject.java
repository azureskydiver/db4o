/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.hierarchy;

import com.db4o.activation.*;

class PrioritizedProject extends Project {

	private int _priority;
	
	public PrioritizedProject(String name, int priority) {
		super(name);
		_priority = priority;
	}

	public int getPriority() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return _priority;
	}
}
