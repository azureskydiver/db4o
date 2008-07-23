/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class ClassMarshaller {
    
    public MarshallerFamily _family;
    
    public RawClassSpec readSpec(Transaction trans,ByteArrayBuffer reader) {
		byte[] nameBytes=readName(trans, reader);
		String className=trans.container().stringIO().read(nameBytes);
		readMetaClassID(reader); // skip
		int ancestorID=reader.readInt();
		reader.incrementOffset(Const4.INT_LENGTH); // index ID
		int numFields=reader.readInt();
		return new RawClassSpec(className,ancestorID,numFields);
    }

    public void write(final Transaction trans, final ClassMetadata clazz, final ByteArrayBuffer writer) {
        
        writer.writeShortString(trans, clazz.nameToWrite());
        
        int intFormerlyKnownAsMetaClassID = 0;
        writer.writeInt(intFormerlyKnownAsMetaClassID);
        
        writer.writeIDOf(trans, clazz.i_ancestor);
        
        writeIndex(trans, clazz, writer);
        
        writer.writeInt(clazz.declaredAspectCount());
        clazz.forEachDeclaredAspect(new Procedure4() {
			public void apply(Object arg) {
				 _family._field.write(trans, clazz, (ClassAspect)arg, writer);
			}
		});
    }

    protected void writeIndex(Transaction trans, ClassMetadata clazz, ByteArrayBuffer writer) {
        int indexID = clazz.index().write(trans);
        writer.writeInt(indexIDForWriting(indexID));
    }
    
    protected abstract int indexIDForWriting(int indexID);

    public byte[] readName(Transaction trans, ByteArrayBuffer reader) {
        byte[] name = readName(trans.container().stringIO(), reader);
        return name;
    }
    
    public final int readMetaClassID(ByteArrayBuffer reader) {
    	return reader.readInt();
    }
    
    private byte[] readName(LatinStringIO sio, ByteArrayBuffer reader) {
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPCLASS);
        }
        int len = reader.readInt();
        len = len * sio.bytesPerChar();
        byte[] nameBytes = new byte[len];
        System.arraycopy(reader._buffer, reader._offset, nameBytes, 0, len);
        nameBytes  = Platform4.updateClassName(nameBytes);
        reader.incrementOffset(len);
        return nameBytes;
    }

    public final void read(ObjectContainerBase stream, ClassMetadata clazz, ByteArrayBuffer reader) {
        clazz.setAncestor(stream.classMetadataForId(reader.readInt()));
        
        if(clazz.callConstructor()){
            // The logic further down checks the ancestor YapClass, whether
            // or not it is allowed, not to call constructors. The ancestor
            // YapClass may possibly have not been loaded yet.
            clazz.createConstructor(stream, clazz.classReflector(), clazz.getName(), true);
        }
        
        clazz.checkType();
        
        readIndex(stream, clazz, reader);
        
        clazz._aspects = createFields(clazz, reader.readInt());
        readFields(stream, reader, clazz._aspects);        
    }

    protected abstract void readIndex(ObjectContainerBase stream, ClassMetadata clazz, ByteArrayBuffer reader) ;

	private ClassAspect[] createFields(ClassMetadata clazz, final int fieldCount) {
		final ClassAspect[] aspects = new ClassAspect[fieldCount];
        for (int i = 0; i < aspects.length; i++) {
            aspects[i] = new FieldMetadata(clazz);
            aspects[i].setHandle(i);
        }
		return aspects;
	}

	private void readFields(ObjectContainerBase stream, ByteArrayBuffer reader, final ClassAspect[] fields) {
		for (int i = 0; i < fields.length; i++) {
            fields[i] = _family._field.read(stream, (FieldMetadata)fields[i], reader);
        }
	}

    public int marshalledLength(final ObjectContainerBase stream, final ClassMetadata clazz) {
        final IntByRef len = new IntByRef(
            stream.stringIO().shortLength(clazz.nameToWrite())
                + Const4.OBJECT_LENGTH
                + (Const4.INT_LENGTH * 2)
                + (Const4.ID_LENGTH));       

        len.value += clazz.index().ownLength();
        
        clazz.forEachDeclaredAspect(new Procedure4() {
            public void apply(Object arg) {
                len.value +=  _family._field.marshalledLength(stream, (ClassAspect)arg);
            }
        });
        return len.value;
    }

	public void defrag(final ClassMetadata classMetadata, final LatinStringIO sio, final DefragmentContextImpl context, int classIndexID)  {
		readName(sio, context.sourceBuffer());
		readName(sio, context.targetBuffer());
		
		int metaClassID=0;
		context.writeInt(metaClassID);

		// ancestor ID
		context.copyID();

		context.writeInt(indexIDForWriting(classIndexID));
		
		// field length
		int numFields = context.readInt();
		
		if(numFields > classMetadata.declaredAspectCount()) {
			throw new IllegalStateException();
		}
		classMetadata.forEachDeclaredAspect(new Procedure4() {
			public void apply(Object arg) {
				ClassAspect aspect = (ClassAspect) arg;
				_family._field.defrag(classMetadata,aspect,sio,context);
			}
		});
	}
}
