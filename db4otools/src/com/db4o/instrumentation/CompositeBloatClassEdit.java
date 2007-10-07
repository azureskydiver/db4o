package com.db4o.instrumentation;

import EDU.purdue.cs.bloat.editor.ClassEditor;


public class CompositeBloatClassEdit implements BloatClassEdit {

	private BloatClassEdit[] _edits;
	
	public CompositeBloatClassEdit(BloatClassEdit[] edits) {
		_edits = edits;
	}

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		InstrumentationStatus status = InstrumentationStatus.NOT_INSTRUMENTED;
		for (int editIdx = 0; editIdx < _edits.length; editIdx++) {
			InstrumentationStatus curStatus = _edits[editIdx].enhance(ce, origLoader, loaderContext);
			status = status.aggregate(curStatus);
			if(!status.canContinue()) {
				break;
			}
		}
		return status;
	}

}
