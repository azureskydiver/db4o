/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.activation;

import com.db4o.internal.*;


public interface ActivationDepthProvider {

	ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode);

	ActivationDepth activationDepth(int depth, ActivationMode mode);
}
