package com.db4o.internal.activation;

public final class ActivationMode {
	
	public static final ActivationMode ACTIVATE = new ActivationMode();
	
	public static final ActivationMode DEACTIVATE = new ActivationMode();
	
	public static final ActivationMode PEEK = new ActivationMode();	
	
	private ActivationMode() {
	}
	
	public String toString() {
		if (this == ACTIVATE) {
			return "ACTIVATE";
		}
		if (this == DEACTIVATE) {
			return "DEACTIVATE";
		}
		return "PEEK";
	}

	public boolean isActivate() {
		return this == ACTIVATE;
	}
}
