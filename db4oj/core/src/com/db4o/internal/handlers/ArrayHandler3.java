package com.db4o.internal.handlers;

import com.db4o.Deploy;
import com.db4o.internal.NullableArrayHandling;
import com.db4o.reflect.ReflectClass;

public class ArrayHandler3 extends ArrayHandler {
 	
    protected boolean isPrimitive(ReflectClass claxx) {
        if(Deploy.csharp){
            return false;
        }
        return claxx.isPrimitive();
    }
	
	protected final boolean useJavaHandling() {
		return ! Deploy.csharp;
	}
	
	protected boolean hasNullBitmap() {
	    return false;
	}
	
	protected boolean readingDotNetBeforeVersion4() {
		if(NullableArrayHandling.enabled() && Deploy.csharp){
			return true;
		}
		return false;
	}	
}