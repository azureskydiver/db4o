package com.db4o.internal.handlers;

import com.db4o.*;

public class ArrayHandler3 extends ArrayHandler {
	
	protected boolean useOldNetHandling() {
		return Deploy.csharp;		
	}
	
	protected boolean useJavaHandling() {
		return !Deploy.csharp;
	}
}
