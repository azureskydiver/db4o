package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;

public class ArrayHandler3 extends ArrayHandler {
	
	protected boolean useOldNetHandling() {
		return Deploy.csharp;		
	}
	
	protected boolean useJavaHandling() {
		return !Deploy.csharp;
	}
	
	protected boolean upgradingDotNetArray() {
		if(!NullableArrayHandling.disabled() && Deploy.csharp){
			return true;
		}
		return false;
	}
	
	protected boolean hasNullBitmap() {
		return false;
	}

}
