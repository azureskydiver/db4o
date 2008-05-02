package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public class ArrayHandler4 extends ArrayHandler {
	
    protected boolean isPrimitive(ReflectClass claxx) {
        return claxx.isPrimitive();
    }
	
	protected boolean useJavaHandling() {
		return true;
	}
	
	protected boolean upgradingDotNetArray() {
		if(!NullableArrayHandling.disabled() && Deploy.csharp){
			return true;
		}
		return false;
	}
	
	protected boolean hasNullBitmap() {
	    return NullableArrayHandling.enabled();
	}

}
