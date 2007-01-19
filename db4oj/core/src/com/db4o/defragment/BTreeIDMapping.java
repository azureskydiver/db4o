/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.mapping.*;

/**
 * BTree mapping for IDs during a defragmentation run.
 * 
 * @see Defragment
 */
public class BTreeIDMapping extends AbstractContextIDMapping {

	private String _fileName;

	private YapFile _mappingDb;

	private BTree _idTree;

	private MappedIDPair _cache = new MappedIDPair(0, 0);

	/**
	 * Will maintain the ID mapping as a BTree in the file with the given path.
	 * If a file exists in this location, it will be DELETED.
	 * 
	 * @param fileName The location where the BTree file should be created.
	 */
	public BTreeIDMapping(String fileName) {
		_fileName = fileName;
	}

	public int mappedID(int oldID, boolean lenient) {
		if (_cache.orig() == oldID) {
			return _cache.mapped();
		}
		int classID = mappedClassID(oldID);
		if (classID != 0) {
			return classID;
		}
		BTreeRange range = _idTree.search(trans(), new MappedIDPair(oldID, 0));
		Iterator4 pointers = range.pointers();
		if (pointers.moveNext()) {
			BTreePointer pointer = (BTreePointer) pointers.current();
			_cache = (MappedIDPair) pointer.key();
			return _cache.mapped();
		}
		if (lenient) {
			return mapLenient(oldID, range);
		}
		return 0;
	}

	private int mapLenient(int oldID, BTreeRange range) {
		range = range.smaller();
		BTreePointer pointer = range.lastPointer();
		if (pointer == null) {
			return 0;
		}
		MappedIDPair mappedIDs = (MappedIDPair) pointer.key();
		return mappedIDs.mapped() + (oldID - mappedIDs.orig());
	}

	protected void mapNonClassIDs(int origID, int mappedID) {
		_cache = new MappedIDPair(origID, mappedID);
		_idTree.add(trans(), _cache);
	}

	public void open() {
		_mappingDb = DefragContextImpl.freshYapFile(_fileName);
		Indexable4 handler = new MappedIDPairHandler(_mappingDb);
		_idTree = new BTree(trans(), 0, handler);
	}

	public void close() {
		_mappingDb.close();
	}

	private Transaction trans() {
		return _mappingDb.getSystemTransaction();
	}
}