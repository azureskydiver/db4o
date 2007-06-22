/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;

public class NativeQueryOptimizerNotLoaded extends DiagnosticBase {

	private int _reason;
	public final static int NQ_NOT_PRESENT 			= 1;
	public final static int NQ_CONSTRUCTION_FAILED 	= 2;

	
	public NativeQueryOptimizerNotLoaded(int reason) {
		_reason = reason;
	}
	public String problem() {
		return "Native Query Optimizer could not be loaded";
	}

	public Object reason() {
		switch (_reason) {
		case NQ_NOT_PRESENT:
			return "Native query not present.";
		case NQ_CONSTRUCTION_FAILED:
			return "Native query couldn't be instantiated.";
		default:
			return "Reason not specified.";
		}
	}

	public String solution() {
		return "If you to have the native queries optimized, please check that the native query jar is present in the class-path.";
	}

}
