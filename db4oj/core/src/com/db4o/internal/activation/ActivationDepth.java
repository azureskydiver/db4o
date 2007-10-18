package com.db4o.internal.activation;

import com.db4o.internal.ClassMetadata;

/**
 * Controls how deep an object graph is activated.
 */
public interface ActivationDepth {
	
	boolean requiresActivation();

	ActivationDepth descend(ClassMetadata metadata);

}
