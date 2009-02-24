package com.db4o.ta.instrumentation;

import java.util.*;

import com.db4o.collections.*;
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
				new InjectTAInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter),
				new ReplaceClassOnInstantiationEdit(ArrayList.class, ActivatableArrayList.class),
		});
	}
}
