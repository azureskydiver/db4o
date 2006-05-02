/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public abstract class StringMarshaller {
    
    protected final int linkLength(){
        return YapConst.YAPINT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
    public Object marshall(Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        
        YapStream stream = a_bytes.getStream();
        String str = (String) a_object;
        int length = stream.stringIO().length(str);
        
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        
        writeShort(stream, str, bytes);
        
        bytes.setID(a_bytes._offset);
        
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.YAPID_LENGTH);
        a_bytes.writeInt(length);
        return bytes;
    }
    
    public static YapReader writeShort(YapStream stream, String str){
        YapReader reader = new YapReader(stream.stringIO().length(str));
        writeShort(stream, str, reader);
        return reader;
    }
    
    public static void writeShort(YapStream stream, String str, YapReader reader){
        int length = str.length();
        if (Deploy.debug) {
            reader.writeBegin(YapConst.YAPSTRING, length);
        }
        reader.writeInt(length);
        stream.stringIO().write(reader, str);
        if (Deploy.debug) {
            reader.writeEnd();
        }
    }
    
    public YapWriter readIndexEntry(YapWriter parentSlot) throws CorruptionException{
        return parentSlot.getStream().readObjectWriterByAddress(parentSlot.getTransaction(), parentSlot.readInt(), parentSlot.readInt());
    }
    
    public String readFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException {
        return read(stream, readSlotFromParentSlot(stream, reader));
    }
    
    // TODO: Instead of working with YapReader objects to transport
    // string buffers, we should consider to have a specific string
    // buffer class, that allows comparisons and carries it's encoding.
    public YapReader readSlotFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException {
        return reader.readEmbeddedObject(stream.getTransaction());
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
    
    public String read(YapStream stream, YapReader reader) throws CorruptionException {
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
    
    public static String readShort(YapStream stream, YapReader a_bytes) throws CorruptionException {
        int length = a_bytes.readInt();
        if (length > YapConst.MAXIMUM_BLOCK_SIZE) {
            throw new CorruptionException();
        }
        if (length > 0) {
            String str = stream.stringIO().read(a_bytes, length);
            if(! Deploy.csharp){
                if(stream.i_config.internStrings()){
                    str = str.intern();
                }
            }
            return str;
        }
        return "";
    }

    public abstract int marshalledLength(YapStream stream, Object obj);


}
