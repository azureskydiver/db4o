/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


public class StringMarshaller1 extends StringMarshaller{
	
    private static final int DEFRAGMENT_INCREMENT_OFFSET = Const4.INT_LENGTH * 2;  
    
    public boolean inlinedStrings(){
        return true;
    }
    
    public BufferImpl readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException{
        int payLoadOffSet = parentSlot.readInt();
        int length = parentSlot.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return parentSlot.readPayloadWriter(payLoadOffSet, length);
    }
    
	public void defrag(DefragmentContext context) {
		context.incrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
	}

}
