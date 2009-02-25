package com.db4o.ta.instrumentation;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.ta.*;

/**
 * Instrumentation step for injecting Transparent Activation awareness by
 * implementing {@link Activatable}.
 */
public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		this(filter, true);
	}

	public InjectTransparentActivationEdit(ClassFilter filter, boolean withCollections) {
		super(createEdits(filter, withCollections));
	}
	
	private static BloatClassEdit[] createEdits(ClassFilter filter, boolean withCollections) {
		BloatClassEdit[] edits = new BloatClassEdit[] {
				new CheckApplicabilityEdit(),
				new InjectTAInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter),
		};
		if(withCollections) {
			BloatClassEdit[] collectionEdit = new BloatClassEdit[]{
					new ReplaceClassOnInstantiationEdit(ArrayList.class, ActivatableArrayList.class),
			};
			edits = (BloatClassEdit[]) Arrays4.merge(edits, collectionEdit, BloatClassEdit.class);
		}
		return edits;
	}
}
