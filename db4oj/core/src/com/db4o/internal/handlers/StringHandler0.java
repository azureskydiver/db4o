/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler0 extends StringHandler {

    public StringHandler0(TypeHandler4 template) {
        super(template);
    }
    
    public Object read(ReadContext context) {
        BufferImpl buffer = readIndirectedBuffer(context); 
        if (buffer == null) {
            return null;
        }
        return readString(context, buffer);
    }
    
    public void delete(DeleteContext context){
    	super.delete(context);
    	context.defragmentRecommended();
    }
    
    public void defragment(DefragmentContext context) {
    	int sourceAddress = context.sourceBuffer().readInt();
    	int length = context.sourceBuffer().readInt();
    	if(sourceAddress == 0 && length == 0) {
        	context.targetBuffer().writeInt(0);
        	context.targetBuffer().writeInt(0);
        	return;
    	}

    	int targetAddress = 0;
    	try {
			targetAddress = context.copySlotToNewMapped(sourceAddress, length);
		} 
    	catch (IOException exc) {
    		throw new Db4oIOException(exc);
		}
    	context.targetBuffer().writeInt(targetAddress);
    	context.targetBuffer().writeInt(length);
    }

}
