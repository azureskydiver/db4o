package com.db4o.internal.activation;

import com.db4o.internal.ClassMetadata;
import com.db4o.reflect.generic.*;
import com.db4o.ta.*;

public class TransparentActivationDepthProvider implements
		ActivationDepthProvider {

	public ActivationDepth activationDepth(int depth, ActivationMode mode) {
		if (Integer.MAX_VALUE == depth) {
			return new FullActivationDepth();
		}
		return new FixedActivationDepth(mode.isPeek() ? depth+1 : depth);
	}

	public ActivationDepth activationDepthFor(ClassMetadata classMetadata, ActivationMode mode) {
		if (isTAAware(classMetadata)) {
			return new NonDescendingActivationDepth();
		}
		if(mode.isPrefetch()){
		    return new FixedActivationDepth(classMetadata.prefetchActivationDepth());
		}
		return new DescendingActivationDepth(this, mode);
	}

	private boolean isTAAware(ClassMetadata classMetadata) {
		GenericReflector reflector = classMetadata.reflector();
		return reflector.forClass(Activatable.class).isAssignableFrom(classMetadata.classReflector());
	}

}
