package com.db4o.internal.activation;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * Activates an object graph to a specific depth respecting any
 * activation configuration settings that might be in effect.
 */
public class LegacyActivationDepth implements ActivationDepth {

	private final int _depth;

	public LegacyActivationDepth(int depth) {
		_depth = depth;
	}

	public ActivationDepth descend(ClassMetadata metadata, ActivationMode mode) {
		if (null == metadata) {
			throw new ArgumentNullException();
		}
		if (null == mode) {
			throw new ArgumentNullException();
		}
		return new LegacyActivationDepth(descendDepth(metadata, mode));
	}

	private int descendDepth(ClassMetadata metadata, ActivationMode mode) {
		if (metadata.isDb4oTypeImpl()) {
	        return 2;
	    }
		int depth = configuredActivationDepth(metadata, mode) - 1;
		if (metadata.isValueType()) {
			// 	We also have to instantiate structs completely every time.
			return Math.max(1, depth);
		}
		return depth;
	}

	private int configuredActivationDepth(ClassMetadata metadata, ActivationMode mode) {
		Config4Class config = metadata.configOrAncestorConfig();
		if (config != null && mode.isActivate()) {
			return config.adjustActivationDepth(_depth);
		}
		return _depth;
	}

	public boolean requiresActivation() {
		return _depth > 0;
	}

}
