/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.mapping.*;

/**
 * @exclude
 */
class BTreeIDMapping extends AbstractIDMapping{
	
		private YapFile _mappingDb;
		
		private BTree _idTree;
		
		private MappedIDPair _cache = new MappedIDPair(0, 0);

		public BTreeIDMapping(String fileName) {
			_mappingDb = DefragContextImpl.freshYapFile(fileName);
			Indexable4 handler = 
				new MappedIDPairHandler(_mappingDb);
			_idTree=new BTree(trans(),0,handler);
		}

		public Integer mappedID(int oldID,boolean lenient) {
			if(_cache.orig() == oldID){
				return new Integer(_cache.mapped());
			}
			Integer classID=mappedClassID(oldID);
			if(classID!=null) {
				return classID;
			}
			BTreeRange range=_idTree.search(trans(),new MappedIDPair(oldID,0));
			Iterator4 pointers=range.pointers();
			if(pointers.moveNext()) {
				BTreePointer pointer=(BTreePointer)pointers.current();
				_cache=(MappedIDPair)pointer.key();
				return new Integer(_cache.mapped());
			}
			if(lenient) {
				return mapLenient(oldID,range);
			}
			return null;
		}

		private Integer mapLenient(int oldID, BTreeRange range) {
			range=range.smaller();
			BTreePointer pointer=range.lastPointer();
			if(pointer==null) {
				return null;
			}
			MappedIDPair mappedIDs = (MappedIDPair) pointer.key();
			return new Integer(mappedIDs.mapped()+(oldID-mappedIDs.orig()));
		}

		public void mapIDs(int oldID,int newID) {
			_cache = new MappedIDPair(oldID,newID);
			_idTree.add(trans(), _cache);
		}

		public void close() {
			_mappingDb.close();
		}

		private Transaction trans() {
			return _mappingDb.getSystemTransaction();
		}
	}