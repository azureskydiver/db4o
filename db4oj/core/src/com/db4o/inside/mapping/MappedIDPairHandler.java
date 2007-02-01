/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.mapping;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.ix.*;

/**
 * @exclude
 */
public class MappedIDPairHandler implements Indexable4 {

	private final YInt _origHandler;
	private final YInt _mappedHandler;
	
	public MappedIDPairHandler(YapStream stream) {
		_origHandler=new YInt(stream);
		_mappedHandler=new YInt(stream);
	}

	public Object comparableObject(Transaction trans, Object indexEntry) {
        throw new NotImplementedException();
	}

	public void defragIndexEntry(ReaderPair readers) {
        throw new NotImplementedException();
	}

	public int linkLength() {
		return _origHandler.linkLength()+_mappedHandler.linkLength();
	}

	public Object readIndexEntry(Buffer reader) {
		int origID=readID(reader);
		int mappedID=readID(reader);
        return new MappedIDPair(origID,mappedID);
	}

	public void writeIndexEntry(Buffer reader, Object obj) {
		MappedIDPair mappedIDs=(MappedIDPair)obj;
		_origHandler.writeIndexEntry(reader, new Integer(mappedIDs.orig()));
		_mappedHandler.writeIndexEntry(reader, new Integer(mappedIDs.mapped()));
	}

	public int compareTo(Object obj) {
        return _origHandler.compareTo(((MappedIDPair)obj).orig());
	}

	public Object current() {
		return new MappedIDPair(_origHandler.currentInt(),_mappedHandler.currentInt());
	}

	public boolean isEqual(Object obj) {
    	throw new NotImplementedException();
	}

	public boolean isGreater(Object obj) {
    	throw new NotImplementedException();
	}

	public boolean isSmaller(Object obj) {
    	throw new NotImplementedException();
	}

	public YapComparable prepareComparison(Object obj) {
        MappedIDPair mappedIDs = (MappedIDPair)obj;
        _origHandler.prepareComparison(mappedIDs.orig());
        _mappedHandler.prepareComparison(mappedIDs.mapped());
        return this;
	}
	
	private int readID(Buffer a_reader) {
		return ((Integer)_origHandler.readIndexEntry(a_reader)).intValue();
	}
}
