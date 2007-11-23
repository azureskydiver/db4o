/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.api.*;

public class BloatFieldRef extends BloatMemberRef implements FieldRef {

	public BloatFieldRef(MemberRef fieldRef) {
		super(fieldRef);
	}
}
