package com.db4o.events;

import com.db4o.internal.*;

public class ClassEventArgs extends EventArgs {

	private ClassMetadata _clazz;

	public ClassEventArgs(ClassMetadata clazz) {
		_clazz = clazz;
	}
	
	public ClassMetadata classMetadata() {
		return _clazz;
	}
}
