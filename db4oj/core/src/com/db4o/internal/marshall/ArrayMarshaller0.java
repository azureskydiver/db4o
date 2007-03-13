/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.io.IOException;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.query.processor.*;
import com.db4o.io.UncheckedIOException;


class ArrayMarshaller0  extends ArrayMarshaller{
    
    public void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer reader)  throws UncheckedIOException {
        int address = reader.readInt();
        int length = reader.readInt();
        if (address <= 0) {
            return;
        }
        Transaction trans = reader.getTransaction();
        if (reader.cascadeDeletes() > 0 && arrayHandler.i_handler instanceof ClassMetadata) {
            StatefulBuffer bytes;
			try {
				bytes = reader.getStream().readWriterByAddress(
                    trans,
                    address,
                    length);
			} catch (IOException e) {
				// FIXME: WILL BE HANDLED IN NEXT SESSION.
				throw new UncheckedIOException(e);
			}
            if (bytes != null) {
                if (Deploy.debug) {
                    bytes.readBegin(arrayHandler.identifier());
                }
                bytes.setCascadeDeletes(reader.cascadeDeletes());
                for (int i = arrayHandler.elementCount(trans, bytes); i > 0; i--) {
                    arrayHandler.i_handler.deleteEmbedded(_family, bytes);
                }
            }
        }
        trans.slotFreeOnCommit(address, address, length);
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, ArrayHandler handler, Object obj, boolean topLevel){
        // do nothing
    }
    
    public Object writeNew(ArrayHandler arrayHandler, Object a_object, boolean topLevel, StatefulBuffer a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        int length = arrayHandler.objectLength(a_object);
        StatefulBuffer bytes = new StatefulBuffer(a_bytes.getTransaction(), length);
        bytes.setUpdateDepth(a_bytes.getUpdateDepth());
        arrayHandler.writeNew1(a_object, bytes);
        bytes.setID(a_bytes._offset);
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(Const4.ID_LENGTH);
        a_bytes.writeInt(length);
        return a_object;
    }
    
    public Object read(ArrayHandler arrayHandler,  StatefulBuffer a_bytes) throws CorruptionException, UncheckedIOException {
        StatefulBuffer bytes = null;
		try {
			bytes = a_bytes.readEmbeddedObject();
		} catch (IOException e) {
			// FIXME: WILL BE HANDLED IN NEXT SESSION.
			throw new UncheckedIOException(e);
		}
        if (bytes == null) {
            return null;
        }
        return arrayHandler.read1(_family, bytes);
    }
    
    public void readCandidates(ArrayHandler arrayHandler, Buffer reader, QCandidates candidates) {
        Buffer bytes = null;
		try {
			bytes = reader.readEmbeddedObject(candidates.i_trans);
		} catch (IOException e) {
			// FIXME: WILL BE HANDLED IN NEXT SESSION.
			throw new UncheckedIOException(e);
		}
        if (bytes == null) {
            return;
        }
        if(Deploy.debug){
            bytes.readBegin(arrayHandler.identifier());
        }
        int count = arrayHandler.elementCount(candidates.i_trans, bytes);
        for (int i = 0; i < count; i++) {
            candidates.addByIdentity(new QCandidate(candidates, null, bytes.readInt(), true));
        }
    }

    
    public final Object readQuery(ArrayHandler arrayHandler, Transaction trans, Buffer reader) throws CorruptionException, UncheckedIOException {
        Buffer bytes = null;
		try {
			bytes = reader.readEmbeddedObject(trans);
		} catch (IOException e) {
			// FIXME: WILL BE HANDLED IN NEXT SESSION.
			throw new UncheckedIOException(e);
		}
        if (bytes == null) {
            return null;
        }
        Object array = arrayHandler.read1Query(trans,_family, bytes);
        return array;
    }
    
    protected Buffer prepareIDReader(Transaction trans,Buffer reader) throws UncheckedIOException {
    	try {
			return reader.readEmbeddedObject(trans);
		} catch (IOException e) {
			// FIXME: WILL BE HANDLED IN NEXT SESSION.
			throw new UncheckedIOException(e);
		}
    }
    
    public void defragIDs(ArrayHandler arrayHandler,ReaderPair readers) {
    }
}
