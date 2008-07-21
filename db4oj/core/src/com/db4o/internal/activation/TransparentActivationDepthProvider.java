package com.db4o.internal.activation;

import com.db4o.internal.*;
import com.db4o.reflect.generic.*;
import com.db4o.ta.*;

public class TransparentActivationDepthProvider implements
		ActivationDepthProvider {

	public ActivationDepth activationDepth(int depth, ActivationMode mode) {
		if (Integer.MAX_VALUE == depth) {
			return new FullActivationDepth(mode);
		}
        return new FixedActivationDepth(depth, mode);
	}

	public ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode) {
		if (isTAAware(classMetadata)) {
			return new NonDescendingActivationDepth(mode);
		}
		if(mode.isPrefetch()){
		    return new FixedActivationDepth(classMetadata.prefetchActivationDepth(), mode);
		}
		return new DescendingActivationDepth(this, mode);
	}

	private boolean isTAAware(ClassMetadata classMetadata) {
		GenericReflector reflector = classMetadata.reflector();
		return reflector.forClass(Activatable.class).isAssignableFrom(classMetadata.classReflector());
	}

}
