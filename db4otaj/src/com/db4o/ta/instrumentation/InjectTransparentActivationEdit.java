package com.db4o.ta.instrumentation;

import com.db4o.instrumentation.core.*;

public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		super(new BloatClassEdit[] {
				new CheckApplicabilityEdit(filter),
				new InjectInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter)
		});
	}
}
