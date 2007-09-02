package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.ClassEditor;

import com.db4o.instrumentation.BloatClassEdit;

public class CompositeBloatClassEdit implements BloatClassEdit {

	private BloatClassEdit[] _edits;
	
	public CompositeBloatClassEdit(BloatClassEdit[] edits) {
		_edits = edits;
	}

	public void bloat(ClassEditor ce) {
		for (int editIdx = 0; editIdx < _edits.length; editIdx++) {
			_edits[editIdx].bloat(ce);
		}
	}

}
