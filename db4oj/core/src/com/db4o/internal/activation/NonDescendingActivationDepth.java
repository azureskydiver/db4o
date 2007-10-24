/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.activation;

import com.db4o.internal.*;

/**
 * Transparent Activation strategy.
 * 
 */
public class NonDescendingActivationDepth implements ActivationDepth {

	public NonDescendingActivationDepth() {
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		return this;
	}

	public boolean requiresActivation() {
		return false;
	}

}
