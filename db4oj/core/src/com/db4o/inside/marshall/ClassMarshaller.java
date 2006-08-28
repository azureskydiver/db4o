/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public class ClassMarshaller {
    
    public MarshallerFamily _family;

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
            _family._field.write(trans, clazz, fields[i], writer);
        }
    }

    public byte[] readName(Transaction trans, YapClass clazz, YapReader reader) {
        byte[] name = readName(trans.stream().stringIO(), reader);
        clazz._metaClassID = reader.readInt();
        return name;
    }
    
    private byte[] readName(YapStringIO sio, YapReader reader) {
        if (Deploy.debug) {
            reader.readBegin(YapConst.YAPCLASS);
        }
        int len = reader.readInt();
        len = len * sio.bytesPerChar();
        byte[] nameBytes = new byte[len];
        System.arraycopy(reader._buffer, reader._offset, nameBytes, 0, len);
        if(Deploy.csharp){
            nameBytes  = Platform4.updateClassName(nameBytes);
        }
        reader.incrementOffset(len);
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
        
        clazz.i_fields = createFields(clazz, reader.readInt());
        readFields(stream, reader, clazz.i_fields);        
    }

	private YapField[] createFields(YapClass clazz, final int fieldCount) {
		final YapField[] fields = new YapField[fieldCount];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new YapField(clazz);
            fields[i].setArrayPosition(i);
        }
		return fields;
	}

	private void readFields(YapStream stream, YapReader reader, final YapField[] fields) {
		for (int i = 0; i < fields.length; i++) {
            fields[i] = _family._field.read(stream, fields[i], reader);
        }
	}

    public int marshalledLength(YapStream stream, YapClass clazz) {
        int len = stream.stringIO().shortLength(clazz.nameToWrite())
                + YapConst.OBJECT_LENGTH
                + (YapConst.INT_LENGTH * 2)
                + (YapConst.ID_LENGTH);       

        len += clazz.index().ownLength();
        
        if (clazz.i_fields != null) {
            for (int i = 0; i < clazz.i_fields.length; i++) {
                len += _family._field.marshalledLength(stream, clazz.i_fields[i]);
            }
        }
        return len;
    }

	public void defrag(YapClass yapClass,YapStringIO sio,YapReader source, YapReader target, IDMapping mapping, int classIndexID) throws CorruptionException {
		readName(sio, source);
		readName(sio, target);
		
		int metaClassOldID = source.readInt();
		int metaClassNewId = 0;
		target.writeInt(metaClassNewId);
		
		int ancestorOldID = source.readInt();
		int ancestorNewId = 0;
		if (ancestorOldID != 0) {
			ancestorNewId = mapping.mappedID(ancestorOldID);
			target.writeInt(ancestorNewId);
		} 
		else {
			target.incrementOffset(YapConst.INT_LENGTH);
		}

		yapClass.index().defragReference(yapClass, source, target, mapping,classIndexID);
		
		source.incrementOffset(YapConst.INT_LENGTH);
		target.incrementOffset(YapConst.INT_LENGTH);
		
		YapField[] fields=yapClass.i_fields;
		for(int fieldIdx=0;fieldIdx<fields.length;fieldIdx++) {
			_family._field.defrag(yapClass,fields[fieldIdx],sio,source,target,mapping);
		}
	}
}
