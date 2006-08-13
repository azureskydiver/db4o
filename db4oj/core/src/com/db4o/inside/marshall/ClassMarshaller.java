/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public class ClassMarshaller {

    public void write(Transaction trans, YapClass clazz, YapReader writer) {
        
        writer.writeShortString(trans, clazz.nameToWrite());
        writer.writeInt(clazz._metaClassID);
        
        writer.writeIDOf(trans, clazz.i_ancestor);
        
        clazz.index().writeId(writer, trans);
        
        YapField[] fields = clazz.i_fields; 
        
        if (fields == null) {
            writer.writeInt(0);
            return;
        } 
        writer.writeInt(fields.length);
        for (int i = 0; i < fields.length; i++) {
            fields[i].writeThis(trans, writer, clazz);
        }
        
    }

    public byte[] readName(Transaction trans, YapClass clazz, YapReader reader) {
        if (Deploy.debug) {
            reader.readBegin(clazz.getIdentifier());
        }
        int len = reader.readInt();
        len = len * trans.stream().stringIO().bytesPerChar();
        byte[] nameBytes = new byte[len];
        System.arraycopy(reader._buffer, reader._offset, nameBytes, 0, len);
        if(Deploy.csharp){
            nameBytes  = Platform4.updateClassName(nameBytes);
        }
        reader.incrementOffset(len);
        clazz._metaClassID = reader.readInt();
        return nameBytes;
    }

    public void read(YapStream stream, YapClass clazz, YapReader reader) {
        clazz.i_ancestor = stream.getYapClass(reader.readInt());
        
        if(clazz.i_dontCallConstructors){
            // The logic further down checks the ancestor YapClass, whether
            // or not it is allowed, not to call constructors. The ancestor
            // YapClass may possibly have not been loaded yet.
            clazz.createConstructor(stream, clazz.classReflector(), clazz.getName(), true);
        }
        
        clazz.checkDb4oType();
        
        clazz.index().read(reader, stream);
        
        clazz.i_fields = new YapField[reader.readInt()];
        for (int i = 0; i < clazz.i_fields.length; i++) {
            clazz.i_fields[i] = new YapField(clazz);
            clazz.i_fields[i].setArrayPosition(i);
        }
        for (int i = 0; i < clazz.i_fields.length; i++) {
            clazz.i_fields[i] = clazz.i_fields[i].readThis(stream, reader);
        }
        
    }

    public int marshalledLength(YapStream stream, YapClass clazz) {
        int len = stream.stringIO().shortLength(clazz.nameToWrite())
                + YapConst.OBJECT_LENGTH
                + (YapConst.YAPINT_LENGTH * 2)
                + (YapConst.YAPID_LENGTH);       

        len += clazz.index().ownLength();
        
        if (clazz.i_fields != null) {
            for (int i = 0; i < clazz.i_fields.length; i++) {
                len += clazz.i_fields[i].ownLength(stream);
            }
        }
        
        return len;
    }

}
