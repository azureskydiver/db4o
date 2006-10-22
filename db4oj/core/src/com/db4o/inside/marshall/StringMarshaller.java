/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public abstract class StringMarshaller {
    
    
    public abstract boolean inlinedStrings();
    
    public abstract void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection);
    
    protected final int linkLength(){
        return YapConst.INT_LENGTH + YapConst.ID_LENGTH;
    }
    
    public abstract Object writeNew(Object a_object, boolean topLevel, YapWriter a_bytes, boolean redirect);
    
    public final String read(YapStream stream, YapReader reader) throws CorruptionException {
        if (reader == null) {
            return null;
        }
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPSTRING);
        }
        String ret = readShort(stream, reader);
        if (Deploy.debug) {
            reader.readEnd();
        }
        return ret;
    }
    
    public String readFromOwnSlot(YapStream stream, YapReader reader){
        try {
            return read(stream, reader);
        } catch (Exception e) {
            if(Deploy.debug || Debug.atHome) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    public String readFromParentSlot(YapStream stream, YapReader reader, boolean redirect) throws CorruptionException {
        if(! redirect){
            return read(stream, reader);
        }
        return read(stream, readSlotFromParentSlot(stream, reader));
    }
    
    public abstract YapReader readIndexEntry(YapWriter parentSlot) throws CorruptionException;
    
    public static String readShort(YapStream stream, YapReader bytes) throws CorruptionException {
    	return readShort(stream.stringIO(),stream.configImpl().internStrings(),bytes);
    }

    public static String readShort(YapStringIO io, boolean internStrings, YapReader bytes) throws CorruptionException {
        int length = bytes.readInt();
        if (length > YapConst.MAXIMUM_BLOCK_SIZE) {
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
    public abstract YapReader readSlotFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException;

    public static YapReader writeShort(YapStream stream, String str){
        YapReader reader = new YapReader(stream.stringIO().length(str));
        writeShort(stream, str, reader);
        return reader;
    }
    
    public static void writeShort(YapStream stream, String str, YapReader reader){
        int length = str.length();
        if (Deploy.debug) {
            reader.writeBegin(YapConst.YAPSTRING);
        }
        reader.writeInt(length);
        stream.stringIO().write(reader, str);
        if (Deploy.debug) {
            reader.writeEnd();
        }
    }

	public abstract void defrag(SlotReader reader);
}
