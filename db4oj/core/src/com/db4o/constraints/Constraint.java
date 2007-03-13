/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.events.*;
import com.db4o.internal.*;

public interface Constraint {
	public void check(ObjectContainerBase objectContainer, EventArgs ea);
}
