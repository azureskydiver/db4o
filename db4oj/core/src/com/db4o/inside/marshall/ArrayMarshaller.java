/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.handlers.*;
import com.db4o.inside.query.processor.*;

/**
 * @exclude
 */
public abstract class ArrayMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer reader);
    
    public final TreeInt collectIDs(ArrayHandler arrayHandler, TreeInt tree, StatefulBuffer reader){
        Transaction trans = reader.getTransaction();
        return arrayHandler.collectIDs1(trans, tree, prepareIDReader(trans,reader));
    }

    public abstract void defragIDs(ArrayHandler arrayHandler,ReaderPair readers);
    
    public abstract void calculateLengths(Transaction trans, ObjectHeaderAttributes header, ArrayHandler handler, Object obj, boolean topLevel);
    
    public abstract Object read(ArrayHandler arrayHandler,  StatefulBuffer reader) throws CorruptionException;
    
    public abstract void readCandidates(ArrayHandler arrayHandler, Buffer reader, QCandidates candidates);
    
    public abstract Object readQuery(ArrayHandler arrayHandler, Transaction trans, Buffer reader) throws CorruptionException;
    
    public abstract Object writeNew(ArrayHandler arrayHandler, Object obj, boolean topLevel, StatefulBuffer writer);

    protected abstract Buffer prepareIDReader(Transaction trans,Buffer reader);
}
