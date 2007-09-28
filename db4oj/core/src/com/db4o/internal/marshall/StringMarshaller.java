/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


public abstract class StringMarshaller {
    
    
    public abstract boolean inlinedStrings();
    
    protected final int linkLength(){
        return Const4.INT_LENGTH + Const4.ID_LENGTH;
    }
    
    public abstract Buffer readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException, IllegalArgumentException, Db4oIOException;
    
    public static String readShort(ObjectContainerBase stream, Buffer bytes) throws CorruptionException {
    	return readShort(stream.stringIO(),stream.configImpl().internStrings(),bytes);
    }

    public static String readShort(LatinStringIO io, boolean internStrings, Buffer bytes) throws CorruptionException {
        int length = bytes.readInt();
        if (length > Const4.MAXIMUM_BLOCK_SIZE) {
            throw new CorruptionException();
        }
        if (length > 0) {
            String str = io.read(bytes, length);
            if(! Deploy.csharp){
                if(internStrings){
                    str = str.intern();
                }
            }
            return str;
        }
        return "";
    }

	public abstract void defrag(SlotBuffer reader);

}
