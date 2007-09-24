package com.db4o.ta.instrumentation;

import com.db4o.instrumentation.BloatClassEdit;
import com.db4o.instrumentation.ClassFilter;
import com.db4o.instrumentation.CompositeBloatClassEdit;

public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		super(new BloatClassEdit[] {
				new CheckApplicabilityEdit(filter),
				new InjectInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter)
		});
	}
}
