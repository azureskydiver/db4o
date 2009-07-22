/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public class StandardReferenceTypeHandler0 extends StandardReferenceTypeHandler{

	@Override
	protected FieldListInfo fieldListFor(MarshallingInfo context) {
		return new FieldListInfo() {
			public boolean isNull(int fieldIndex) {
				return false;
			}
		};
    }

}
