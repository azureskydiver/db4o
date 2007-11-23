/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;

public class BloatMemberRef {

	protected final MemberRef _method;

	public BloatMemberRef(MemberRef memberRef) {
		_method = memberRef;
	}

	public MemberRef member() {
		return _method;
	}

}