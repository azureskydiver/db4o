/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.diagnostic;

/**
 * Diagnostic on failed delete.
 */
public class DeletionFailed extends DiagnosticBase {
	
	public String problem() {
		return "Cascading delete to members failed. Possible reasons: db4o engine updates, corruption, changed class hierarchies.";
	}
	
	public Object reason() {
		return "";
	}
	
	public String solution() {
		return "Running Defragment may fix.";
	}
}
