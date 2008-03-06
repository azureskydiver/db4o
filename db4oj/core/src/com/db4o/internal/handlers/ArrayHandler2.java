/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;



/**
 * @exclude
 */
public class ArrayHandler2 extends ArrayHandler {
    
    protected int preparePayloadRead(DefragmentContext context) {
    	if(MarshallingLogicSimplification.enabled){
    		return context.offset();
    	}
        int newPayLoadOffset = context.readInt();
        context.readInt();  // skip length, not needed
        int linkOffSet = context.offset();
        context.seek(newPayLoadOffset);
        return linkOffSet;
    }

}
