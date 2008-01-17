/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.ta;

import com.db4o.*;
import com.db4o.ext.*;

public interface RollbackStrategy {
	
	void rollback(ObjectContainer container, ObjectInfo o);

}
