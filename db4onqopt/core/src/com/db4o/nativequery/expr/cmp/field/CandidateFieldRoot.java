/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class CandidateFieldRoot extends ComparisonOperandRoot {
	public final static CandidateFieldRoot INSTANCE=new CandidateFieldRoot();
	
	private CandidateFieldRoot() {}
	
	public String toString() {
		return "CANDIDATE";
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
