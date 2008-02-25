/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures.dynamic;

import com.db4o.foundation.*;

public interface FixtureProvider extends Iterable4 {
	
	public ContextVariable variable();
	
}
