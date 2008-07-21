/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.internal.query.result.*;

public interface QueryResultIteratorFactory {
	
	Iterator4 newInstance(AbstractQueryResult result);

}
