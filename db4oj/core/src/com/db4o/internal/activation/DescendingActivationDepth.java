package com.db4o.internal.activation;

import com.db4o.internal.*;

public class DescendingActivationDepth extends ActivationDepthImpl {

	private final TransparentActivationDepthProvider _provider;
	
	public DescendingActivationDepth(TransparentActivationDepthProvider provider, ActivationMode mode) {
		super(mode);
		_provider = provider;
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		return _provider.activationDepthFor(metadata, _mode);
	}

	public boolean requiresActivation() {
		return true;
	}

}
