/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation;

import com.db4o.instrumentation.BloatClassEdit;
import com.db4o.instrumentation.ClassFilter;
import com.db4o.instrumentation.CompositeBloatClassEdit;

public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		super(new BloatClassEdit[] {new InjectInfrastructureEdit(filter), new InstrumentMethodStartEdit()});
	}
}
