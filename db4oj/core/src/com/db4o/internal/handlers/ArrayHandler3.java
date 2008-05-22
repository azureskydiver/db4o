package com.db4o.internal.handlers;

import com.db4o.Deploy;
import com.db4o.internal.*;
import com.db4o.reflect.*;

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