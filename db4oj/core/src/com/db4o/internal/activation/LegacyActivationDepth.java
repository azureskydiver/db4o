package com.db4o.internal.activation;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * Activates an object graph to a specific depth respecting any
 * activation configuration settings that might be in effect.
 */
public class LegacyActivationDepth implements ActivationDepth {

	private final int _depth;
	private ActivationMode _mode;
	
	public LegacyActivationDepth(int depth) {
		this(depth, ActivationMode.ACTIVATE);
	}

	public LegacyActivationDepth(int depth, ActivationMode mode) {
		if (null == mode) {
			throw new ArgumentNullException();
		}
		_depth = depth;
		_mode = mode;
	}

	public ActivationDepth descend(ClassMetadata metadata) {
		if (null == metadata) {
			throw new ArgumentNullException();
		}
		return new LegacyActivationDepth(descendDepth(metadata), _mode);
	}

	private int descendDepth(ClassMetadata metadata) {
//		if (metadata.isDb4oTypeImpl()) {
//	        return 2;
//	    }
		int depth = configuredActivationDepth(metadata) - 1;
		if (metadata.isValueType()) {
			// 	We also have to instantiate structs completely every time.
			return Math.max(1, depth);
		}
		return depth;
	}

	private int configuredActivationDepth(ClassMetadata metadata) {
		Config4Class config = metadata.configOrAncestorConfig();
		if (config != null && _mode.isActivate()) {
			return config.adjustActivationDepth(_depth);
		}
		return _depth;
	}

	public boolean requiresActivation() {
		if (_mode.isPeek()) {
			return _depth >= 0;
		}
		return _depth > 0;
	}

}
