package com.db4o.internal.activation;

import com.db4o.internal.ClassMetadata;

/**
 * Activates a fixed depth of the object graph regardless of
 * any existing activation depth configuration settings.
 */
public class FixedActivationDepth implements ActivationDepth {

	private final int _depth;

	public FixedActivationDepth(int depth) {
		_depth = depth;
	}
	
	public boolean requiresActivation() {
		return _depth > 0;
	}
	
	public ActivationDepth descend(ClassMetadata metadata, ActivationMode mode) {
		if (_depth < 1) {
			return this;
		}
		return new FixedActivationDepth(_depth-1);
	}

}
