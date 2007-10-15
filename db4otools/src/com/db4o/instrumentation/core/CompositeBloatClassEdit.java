package com.db4o.instrumentation.core;

import EDU.purdue.cs.bloat.editor.ClassEditor;


public class CompositeBloatClassEdit implements BloatClassEdit {

	private final BloatClassEdit[] _edits;
	private final boolean _ignoreFailure;
	
	public CompositeBloatClassEdit(BloatClassEdit[] edits) {
		this(edits, false);
	}

	public CompositeBloatClassEdit(BloatClassEdit[] edits, boolean ignoreFailure) {
		_edits = edits;
		_ignoreFailure = ignoreFailure;
	}

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		InstrumentationStatus status = InstrumentationStatus.NOT_INSTRUMENTED;
		for (int editIdx = 0; editIdx < _edits.length; editIdx++) {
			InstrumentationStatus curStatus = _edits[editIdx].enhance(ce, origLoader, loaderContext);
			// TODO
			loaderContext.commit();
			status = status.aggregate(curStatus, _ignoreFailure);
			if(!_ignoreFailure && !status.canContinue()) {
				break;
			}
		}
		return status;
	}

}
