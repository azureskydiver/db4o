package com.db4o.internal.handlers;

import com.db4o.Deploy;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public class ArrayHandler3 extends ArrayHandler {
 	
    protected boolean isPrimitive(ReflectClass claxx) {
        
        if(Deploy.csharp){
            // TODO: Check if this is correct. 
            // In this case we may get the nullable type
            // associated with this arrayhandler, but
            // we always want to use the non-nullable
            // type if we read with the old arrayHandler.
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
	
	protected ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
	    if(Deploy.csharp && NullableArrayHandling.enabled()){
	        ReflectClass primitiveClaxx = Handlers4.primitiveClassReflector(classMetadata, reflector);
	        if(primitiveClaxx != null){
	            return primitiveClaxx;
	        }
	    }
	    return super.classReflector(reflector, classMetadata, isPrimitive);
	}

}