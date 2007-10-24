package com.db4o.internal.activation;

public final class ActivationMode {
	
	public static final ActivationMode ACTIVATE = new ActivationMode();
	
	public static final ActivationMode DEACTIVATE = new ActivationMode();
	
	public static final ActivationMode PEEK = new ActivationMode();

	public static final ActivationMode PREFETCH = new ActivationMode();	
	
	private ActivationMode() {
	}
	
	public String toString() {
		if (this == ACTIVATE) {
			return "ACTIVATE";
		}
		if (this == DEACTIVATE) {
			return "DEACTIVATE";
		}
		if (this == PREFETCH) {
			return "PREFETCH";
		}
		return "PEEK";
	}

	public boolean isActivate() {
		return this == ACTIVATE;
	}

	public boolean isPeek() {
		return this == PEEK;
	}

	public boolean isPrefetch() {
		return this == PREFETCH;
	}
}
