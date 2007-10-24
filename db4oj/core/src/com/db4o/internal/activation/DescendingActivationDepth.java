package com.db4o.internal.activation;

import com.db4o.internal.*;

public class DescendingActivationDepth implements ActivationDepth {

	private final TransparentActivationDepthProvider _provider;
	private final ActivationMode _mode;
	
	public DescendingActivationDepth(TransparentActivationDepthProvider provider, ActivationMode mode) {
		_provider = provider;
		_mode = mode;
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		return _provider.activationDepthFor(metadata, _mode);
	}

	public boolean requiresActivation() {
		return true;
	}

}
