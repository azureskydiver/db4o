/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;


interface YapDataType extends YapComparable
{
	
	void appendEmbedded3(YapWriter a_bytes);
	
	boolean canHold(Class a_class);
	
	void cascadeActivation(Transaction a_trans, Object a_object, int a_depth, boolean a_activate);
	
	// special construct for deriving from simple types
	void copyValue(Object a_from, Object a_to);
	
	void deleteEmbedded(YapWriter a_bytes);
	
	int getID();
	
	Class getPrimitiveJavaClass();
	
	IClass classReflector();
	
	boolean equals(YapDataType a_dataType); // needed for YapField.equals
	
	Object indexEntry(Object a_object);
	
	Object indexObject(Transaction a_trans, Object a_object);
	
	int linkLength();
	
	void prepareLastIoComparison(Transaction a_trans, Object obj);
	
	Object read(YapWriter writer) throws CorruptionException;
	
	Object readIndexObject(YapWriter writer) throws CorruptionException;
	
	Object readQuery(Transaction trans, YapReader reader, boolean toArray) throws CorruptionException;
	
	boolean supportsIndex();
	
	void writeNew(Object a_object, YapWriter a_bytes);
	
	public int getType ();
	
	YapClass getYapClass(YapStream a_stream);
	
	void readCandidates(YapReader a_bytes, QCandidates a_candidates);
	
	Object readIndexEntry(YapReader a_reader) ;
	
	YapDataType readArrayWrapper(Transaction a_trans, YapReader[] a_bytes);
	
	void writeIndexEntry(YapWriter a_writer, Object a_object);

	
}
