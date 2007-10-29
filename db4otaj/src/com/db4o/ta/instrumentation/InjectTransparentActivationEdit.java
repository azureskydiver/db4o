package com.db4o.ta.instrumentation;

import com.db4o.instrumentation.core.*;
import com.db4o.ta.*;

/**
 * Instrumentation step for injecting Transparent Activation awareness by
 * implementing {@link Activatable}.
 */
public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		super(new BloatClassEdit[] {
				new CheckApplicabilityEdit(),
				new InjectInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter)
		});
	}
}
