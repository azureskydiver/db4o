package com.db4o.db4ounit.common.activation;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;

/**
 * An ActivationDepthProvider that records ActivationDepthProvider calls and
 * delegates to another provider.
 */
public class MockActivationDepthProvider extends MethodCallRecorder implements ActivationDepthProvider {
	
	private final ActivationDepthProvider _delegate;
	
	public MockActivationDepthProvider() {
		_delegate = LegacyActivationDepthProvider.INSTANCE;
	}

	public ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode) {
		record(new MethodCall("activationDepthFor", classMetadata, mode));
		return _delegate.activationDepthFor(classMetadata, mode);
	}

	public ActivationDepth activationDepth(int depth) {
		record(new MethodCall("activationDepth", new Integer(depth)));
		return _delegate.activationDepth(depth);
	}
}
