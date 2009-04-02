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
		BloatClassEdit[] firstSet = new BloatClassEdit[] {
				new CheckApplicabilityEdit(),
		};
		BloatClassEdit[] secondSet = new BloatClassEdit[] {
				new InjectTAInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter),
		};
		BloatClassEdit[] edits = firstSet;
		if(withCollections) {
			BloatClassEdit[] collectionEdit = new BloatClassEdit[]{
					new ReplaceClassOnInstantiationEdit(new ClassReplacementSpec[] {
							new ClassReplacementSpec(ArrayList.class, ActivatableArrayList.class),
							new ClassReplacementSpec(HashMap.class, ActivatableHashMap.class),
							new ClassReplacementSpec(LinkedList.class, ActivatableLinkedList.class),
							new ClassReplacementSpec(Stack.class, ActivatableStack.class),
					}),
			};
			edits = (BloatClassEdit[]) Arrays4.merge(edits, collectionEdit, BloatClassEdit.class);
		}
		edits = (BloatClassEdit[]) Arrays4.merge(edits, secondSet, BloatClassEdit.class);
		return edits;
	}
}
