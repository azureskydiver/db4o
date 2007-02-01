/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public interface TypeHandler4 extends Indexable4
{
	
	boolean canHold(ReflectClass claxx);
	
	void cascadeActivation(Transaction a_trans, Object a_object, int a_depth, boolean a_activate);
	
	ReflectClass classReflector();
    
    Object coerce(ReflectClass claxx, Object obj);
	
	// special construct for deriving from simple types
	void copyValue(Object a_from, Object a_to);
	
	void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes);
	
	int getID();
	
	boolean equals(TypeHandler4 a_dataType); // needed for YapField.equals
	
    boolean hasFixedLength();
    
    boolean indexNullHandling();
    
    int isSecondClass();
    
    /**
     * The length calculation is different, depending from where we 
     * calculate. If we are still in the link area at the beginning of
     * the slot, no data needs to be written to the payload area for
     * primitive types, since they fully fit into the link area. If
     * we are already writing something like an array (or deeper) to
     * the payload area when we come here, a primitive does require
     * space in the payload area.
     * Differentiation is expressed with the 'topLevel' parameter.
     * If 'topLevel==true' we are asking for a size calculation for
     * the link area. If 'topLevel==false' we are asking for a size
     * calculation for the payload area at the end of the slot.
     */
    void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection);
    
    Object indexEntryToObject(Transaction trans, Object indexEntry);
	
	void prepareComparison(Transaction a_trans, Object obj);
	
	ReflectClass primitiveClassReflector();
	
	Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect) throws CorruptionException;
    
	Object readIndexEntry(MarshallerFamily mf, StatefulBuffer writer) throws CorruptionException;
	
	Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer reader, boolean toArray) throws CorruptionException;
	
	boolean supportsIndex();
	
    Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkOffset);
	
	public int getTypeID ();
	
	YapClass getYapClass(YapStream a_stream);
    
    /**
     * performance optimized read (only used for byte[] so far) 
     */
    boolean readArray(Object array, Buffer reader);
	
	void readCandidates(MarshallerFamily mf, Buffer reader, QCandidates candidates);
	
	TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes);
	
    /**
     * performance optimized write (only used for byte[] so far) 
     */
    boolean writeArray(Object array, Buffer reader);

    QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection);

	void defrag(MarshallerFamily mf, ReaderPair readers, boolean redirect);
}
