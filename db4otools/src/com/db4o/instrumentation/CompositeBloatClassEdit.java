package com.db4o.instrumentation;

import EDU.purdue.cs.bloat.editor.ClassEditor;


public class CompositeBloatClassEdit implements BloatClassEdit {

	private BloatClassEdit[] _edits;
	
	public CompositeBloatClassEdit(BloatClassEdit[] edits) {
		_edits = edits;
	}

	public boolean bloat(ClassEditor ce, ClassLoader origLoader) {
		for (int editIdx = 0; editIdx < _edits.length; editIdx++) {
			boolean succeeded = _edits[editIdx].bloat(ce, origLoader);
			if(!succeeded) {
				return false;
			}
		}
		return true;
	}

}
