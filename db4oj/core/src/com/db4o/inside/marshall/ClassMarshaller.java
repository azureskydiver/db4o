/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public abstract class ClassMarshaller {
    
    public MarshallerFamily _family;
    
    public RawClassSpec readSpec(Transaction trans,Buffer reader) {
		byte[] nameBytes=readName(trans, reader);
		String className=trans.stream().stringIO().read(nameBytes);
		readMetaClassID(reader); // skip
		int ancestorID=reader.readInt();
		reader.incrementOffset(YapConst.INT_LENGTH); // index ID
		int numFields=reader.readInt();
		return new RawClassSpec(className,ancestorID,numFields);
    }

    public void write(Transaction trans, ClassMetadata clazz, Buffer writer) {
        
        writer.writeShortString(trans, clazz.nameToWrite());
        
        int intFormerlyKnownAsMetaClassID = 0;
        writer.writeInt(intFormerlyKnownAsMetaClassID);
        
        writer.writeIDOf(trans, clazz.i_ancestor);
        
        writeIndex(trans, clazz, writer);
        
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

    protected void writeIndex(Transaction trans, ClassMetadata clazz, Buffer writer) {
        int indexID = clazz.index().write(trans);
        writer.writeInt(indexIDForWriting(indexID));
    }
    
    protected abstract int indexIDForWriting(int indexID);

    public byte[] readName(Transaction trans, Buffer reader) {
        byte[] name = readName(trans.stream().stringIO(), reader);
        return name;
    }
    
    public int readMetaClassID(Buffer reader) {
    	return reader.readInt();
    }
    
    private byte[] readName(YapStringIO sio, Buffer reader) {
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

    public void read(ObjectContainerBase stream, ClassMetadata clazz, Buffer reader) {
        clazz.i_ancestor = stream.getYapClass(reader.readInt());
        
        if(clazz.i_dontCallConstructors){
            // The logic further down checks the ancestor YapClass, whether
            // or not it is allowed, not to call constructors. The ancestor
            // YapClass may possibly have not been loaded yet.
            clazz.createConstructor(stream, clazz.classReflector(), clazz.getName(), true);
        }
        
        clazz.checkType();
        
        readIndex(stream, clazz, reader);
        
        clazz.i_fields = createFields(clazz, reader.readInt());
        readFields(stream, reader, clazz.i_fields);        
    }

    protected abstract void readIndex(ObjectContainerBase stream, ClassMetadata clazz, Buffer reader) ;

	private YapField[] createFields(ClassMetadata clazz, final int fieldCount) {
		final YapField[] fields = new YapField[fieldCount];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new YapField(clazz);
            fields[i].setArrayPosition(i);
        }
		return fields;
	}

	private void readFields(ObjectContainerBase stream, Buffer reader, final YapField[] fields) {
		for (int i = 0; i < fields.length; i++) {
            fields[i] = _family._field.read(stream, fields[i], reader);
        }
	}

    public int marshalledLength(ObjectContainerBase stream, ClassMetadata clazz) {
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

	public void defrag(ClassMetadata yapClass,YapStringIO sio,ReaderPair readers, int classIndexID) throws CorruptionException {
		readName(sio, readers.source());
		readName(sio, readers.target());
		
		int metaClassID=0;
		readers.writeInt(metaClassID);

		// ancestor ID
		readers.copyID();

		readers.writeInt(indexIDForWriting(classIndexID));
		
		// field length
		readers.incrementIntSize();
		
		YapField[] fields=yapClass.i_fields;
		for(int fieldIdx=0;fieldIdx<fields.length;fieldIdx++) {
			_family._field.defrag(yapClass,fields[fieldIdx],sio,readers);
		}
	}
}
