/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class UntypedMarshaller0 extends UntypedMarshaller {
    
    public boolean useNormalClassRead(){
        return true;
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, BufferImpl[] a_bytes) {
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
