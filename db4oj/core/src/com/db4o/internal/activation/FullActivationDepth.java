package com.db4o.internal.activation;

import com.db4o.internal.ClassMetadata;

/**
 * Activates the full object graph.
 */
public class FullActivationDepth implements ActivationDepth {

	public ActivationDepth descend(ClassMetadata metadata, ActivationMode mode) {
		return this;
	}

	public boolean requiresActivation() {
		return true;
	}

}
