/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public interface TypeHandler4 extends Indexable4
{
	
	void appendEmbedded3(YapWriter a_bytes);
		
	boolean canHold(ReflectClass claxx);
	
	void cascadeActivation(Transaction a_trans, Object a_object, int a_depth, boolean a_activate);
	
	ReflectClass classReflector();
    
    Object coerce(ReflectClass claxx, Object obj);
	
	// special construct for deriving from simple types
	void copyValue(Object a_from, Object a_to);
	
	void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes);
	
	int getID();
	
	boolean equals(TypeHandler4 a_dataType); // needed for YapField.equals
	
    boolean hasFixedLength();
    
    boolean indexNullHandling();
    
    int isSecondClass();
    
    int marshalledLength(Object obj);
	
	void prepareComparison(Transaction a_trans, Object obj);
	
	ReflectClass primitiveClassReflector();
	
	Object read(MarshallerFamily mf, YapWriter writer) throws CorruptionException;
    
	Object readIndexEntry(MarshallerFamily mf, YapWriter writer) throws CorruptionException;
	
	Object readQuery(Transaction trans, MarshallerFamily mf, YapReader reader, boolean toArray) throws CorruptionException;
	
	boolean supportsIndex();
	
    // FIXME: SM MarshallerFamily does not need to be passed. All can use Mf#current()
    Object writeNew(MarshallerFamily mf, Object a_object, YapWriter a_bytes);
	
	public int getTypeID ();
	
	YapClass getYapClass(YapStream a_stream);
    
    /**
     * performance optimized read (only used for byte[] so far) 
     */
    boolean readArray(Object array, YapWriter reader);
	
	void readCandidates(YapReader a_bytes, QCandidates a_candidates);
	
	TypeHandler4 readArrayWrapper(Transaction a_trans, YapReader[] a_bytes);
	
    /**
     * performance optimized write (only used for byte[] so far) 
     */
    boolean writeArray(Object array, YapWriter reader);
    

	
}
