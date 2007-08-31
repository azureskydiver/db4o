/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


public abstract class StringMarshaller {
    
    
    public abstract boolean inlinedStrings();
    
    protected final int linkLength(){
        return Const4.INT_LENGTH + Const4.ID_LENGTH;
    }
    
    public final String read(ObjectContainerBase stream, Buffer reader) throws CorruptionException {
        if (reader == null) {
            return null;
        }
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPSTRING);
        }
        String ret = readShort(stream, reader);
        if (Deploy.debug) {
            reader.readEnd();
        }
        return ret;
    }
    
    public String readFromOwnSlot(ObjectContainerBase stream, Buffer reader){
        try {
            return read(stream, reader);
        } catch (Exception e) {
            if(Deploy.debug || Debug.atHome) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    public String readFromParentSlot(ObjectContainerBase stream, Buffer reader, boolean redirect) throws CorruptionException, Db4oIOException {
        if(redirect){
    		return read(stream, readSlotFromParentSlot(stream, reader));
        }
        return read(stream, reader);
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

    // TODO: Instead of working with YapReader objects to transport
    // string buffers, we should consider to have a specific string
    // buffer class, that allows comparisons and carries it's encoding.
    public abstract Buffer readSlotFromParentSlot(ObjectContainerBase stream, Buffer reader) throws CorruptionException, Db4oIOException;

    public static Buffer writeShort(ObjectContainerBase stream, String str){
        Buffer reader = new Buffer(stream.stringIO().length(str));
        writeShort(stream, str, reader);
        return reader;
    }
    
    public static void writeShort(ObjectContainerBase stream, String str, Buffer reader){
        int length = str.length();
        if (Deploy.debug) {
            reader.writeBegin(Const4.YAPSTRING);
        }
        reader.writeInt(length);
        stream.stringIO().write(reader, str);
        if (Deploy.debug) {
            reader.writeEnd();
        }
    }

	public abstract void defrag(SlotBuffer reader);

}
