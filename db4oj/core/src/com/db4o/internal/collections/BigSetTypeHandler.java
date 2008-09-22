/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.collections;

import com.db4o.collections.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 * @decaf.ignore
 */
public class BigSetTypeHandler implements TypeHandler4{

	public void defragment(DefragmentContext context) {
		// TODO Auto-generated method stub
		
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		// TODO Auto-generated method stub
		
	}

	public Object read(ReadContext context) {
		BigSet bigSet = (BigSet)((UnmarshallingContext)context).persistentObject();
		bigSet.read(context);
		return bigSet;
	}

	public void write(WriteContext context, Object obj) {
		BigSet bigSet = (BigSet) obj;
		bigSet.write(context);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
