/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.activation;

import com.db4o.internal.*;

/**
 * Transparent Activation strategy.
 * 
 * Currently just behaves the same as LegacyActivationDepth(1).
 * 
 */
public class TransparentActivationDepth implements ActivationDepth {
	
	private final LegacyActivationDepth _delegate;

	public TransparentActivationDepth() {
		_delegate = new LegacyActivationDepth(1, ActivationMode.ACTIVATE);
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		return _delegate.descend(metadata);
	}

	public boolean requiresActivation() {
		return _delegate.requiresActivation();
	}

}
