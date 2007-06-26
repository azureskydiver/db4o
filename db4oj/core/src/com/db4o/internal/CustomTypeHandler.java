/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class CustomTypeHandler implements TypeHandler4{

    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header,
        boolean topLevel, Object obj, boolean withIndirection) {
        // TODO Auto-generated method stub
        
    }

    public boolean canHold(ReflectClass claxx) {
        // TODO Auto-generated method stub
        return false;
    }

    public void cascadeActivation(Transaction a_trans, Object a_object, int a_depth,
        boolean a_activate) {
        // TODO Auto-generated method stub
        
    }

    public ReflectClass classReflector() {
        // TODO Auto-generated method stub
        return null;
    }

    public void defrag(MarshallerFamily mf, ReaderPair readers, boolean redirect) {
        // TODO Auto-generated method stub
        
    }

    public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) throws Db4oIOException {
        // TODO Auto-generated method stub
        
    }

    public int getID() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean hasFixedLength() {
        // TODO Auto-generated method stub
        return false;
    }

    public int linkLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect)
        throws CorruptionException, Db4oIOException {
        // TODO Auto-generated method stub
        return null;
    }

    public void readCandidates(MarshallerFamily mf, Buffer reader, QCandidates candidates)
        throws Db4oIOException {
        // TODO Auto-generated method stub
        
    }

    public Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection,
        Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException {
        // TODO Auto-generated method stub
        return null;
    }

    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates,
        boolean withIndirection) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel,
        StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    public int compareTo(Object obj) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object current() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEqual(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isGreater(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSmaller(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    public Comparable4 prepareComparison(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }
    
//  CustomTypeHandler(ObjectContainerBase container, ReflectClass reflector) {
//  super(container, reflector);
//  // TODO Auto-generated constructor stub
//}



}
