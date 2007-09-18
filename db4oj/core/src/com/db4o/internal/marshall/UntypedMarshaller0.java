/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class UntypedMarshaller0 extends UntypedMarshaller {
    
    public void deleteEmbedded(StatefulBuffer parentBytes) throws Db4oIOException {
        int objectID = parentBytes.readInt();
        if (objectID > 0) {
            StatefulBuffer reader =
                parentBytes.getStream().readWriterByID(parentBytes.getTransaction(), objectID);
            if (reader != null) {
                reader.setCascadeDeletes(parentBytes.cascadeDeletes());
                ObjectHeader oh = new ObjectHeader(reader);
                if(oh.classMetadata() != null){
                    oh.classMetadata().deleteEmbedded1(_family, reader, objectID);
                }
            }
        }
    }
    
    public boolean useNormalClassRead(){
        return true;
    }


    public Object read(StatefulBuffer reader) {
        throw Exceptions4.shouldNeverBeCalled();
    }
    
    public Object readQuery(Transaction trans, Buffer reader, boolean toArray) throws CorruptionException{
        throw Exceptions4.shouldNeverBeCalled();
    }

    
    public TypeHandler4 readArrayHandler(Transaction a_trans, Buffer[] a_bytes) {
        int id = 0;

        int offset = a_bytes[0]._offset;
        try {
            id = a_bytes[0].readInt();
        } catch (Exception e) {
        }
        a_bytes[0]._offset = offset;

        if (id != 0) {
            StatefulBuffer reader =
                a_trans.container().readWriterByID(a_trans, id);
            if (reader != null) {
                ObjectHeader oh = new ObjectHeader(reader);
                try {
                    if (oh.classMetadata() != null) {
                        a_bytes[0] = reader;
                        return oh.classMetadata().readArrayHandler1(a_bytes);
                    }
                } catch (Exception e) {
                    
                    if(Debug.atHome){
                        e.printStackTrace();
                    }
                    
                    // TODO: Check Exception Types
                    // Errors typically occur, if classes don't match
                }
            }
        }
        return null;
    }

	public void defrag(BufferPair readers) {
		// TODO
	}
}
