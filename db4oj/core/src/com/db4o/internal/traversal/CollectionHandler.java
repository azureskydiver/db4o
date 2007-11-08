/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.traversal;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

public interface CollectionHandler {
	
	boolean canHandle(ReflectClass claxx);
	
	Iterator4 iteratorFor(Object collection);
}
