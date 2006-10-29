/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

public class MappedIDPairHandler implements Indexable4 {

	private final YInt _origHandler;
	private final YInt _mappedHandler;
	private final YBoolean _seenHandler;
	
	public MappedIDPairHandler(YapStream stream) {
		_origHandler=new YInt(stream);
		_mappedHandler=new YInt(stream);
		_seenHandler=new YBoolean(stream);
	}

	public Object comparableObject(Transaction trans, Object indexEntry) {
        throw new NotImplementedException();
	}

	public void defragIndexEntry(ReaderPair readers) {
        throw new NotImplementedException();
	}

	public int linkLength() {
		return _origHandler.linkLength()+_mappedHandler.linkLength()+_seenHandler.linkLength();
	}

	public Object readIndexEntry(YapReader reader) {
		int origID=readID(reader);
		int mappedID=readID(reader);
		boolean seen=readSeen(reader);
        return new MappedIDPair(origID,mappedID,seen);
	}

	public void writeIndexEntry(YapReader reader, Object obj) {
		MappedIDPair mappedIDs=(MappedIDPair)obj;
		_origHandler.writeIndexEntry(reader, new Integer(mappedIDs.orig()));
		_origHandler.writeIndexEntry(reader, new Integer(mappedIDs.mapped()));
		_seenHandler.writeIndexEntry(reader,(mappedIDs.seen() ? Boolean.TRUE : Boolean.FALSE));
	}

	public int compareTo(Object obj) {
    	if (null == obj) {
    		throw new ArgumentNullException();
    	}
        MappedIDPair mappedIDs = (MappedIDPair)obj;
        int result = _origHandler.compareTo(mappedIDs.orig());
		return result;
	}

	public Object current() {
		return new MappedIDPair(_origHandler.currentInt(),_mappedHandler.currentInt(),((Boolean)_seenHandler.current()).booleanValue());
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
        _seenHandler.prepareComparison((mappedIDs.seen() ? Boolean.TRUE : Boolean.FALSE));
        return this;
	}
	
	private int readID(YapReader a_reader) {
		return ((Integer)_origHandler.readIndexEntry(a_reader)).intValue();
	}

	private boolean readSeen(YapReader a_reader) {
		return ((Boolean)_seenHandler.readIndexEntry(a_reader)).booleanValue();
	}
}
