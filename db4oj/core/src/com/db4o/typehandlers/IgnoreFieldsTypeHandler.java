/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

/**
 * Typehandler that ignores all fields on a class
 */
public class IgnoreFieldsTypeHandler implements TypeHandler4, FirstClassHandler{

	public void defragment(DefragmentContext context) {
		// do nothing
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		// do nothing
	}

	public Object read(ReadContext context) {
		return null;
	}

	public void write(WriteContext context, Object obj) {
		// do nothing
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		return null;
	}

	public void cascadeActivation(ActivationContext4 context) {
		// do nothing
	}

	public void collectIDs(QueryingReadContext context) {
		// do nothing
	}

	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
		return null;
	}

	public boolean canHold(ReflectClass type) {
		return true;
    }

}
