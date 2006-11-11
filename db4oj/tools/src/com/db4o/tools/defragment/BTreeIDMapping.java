/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.mapping.*;

class BTreeIDMapping {
	
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
			Integer classID=(Integer)_classIDs.get(oldID);
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
			Integer value=null;
			MappedIDPair mappedIDs=null;
			range=range.smaller();
			BTreePointer pointer=range.lastPointer();
			if(pointer!=null) {
				mappedIDs=(MappedIDPair) pointer.key();
			}
			if(mappedIDs!=null) {
				value=new Integer(mappedIDs.mapped()+(oldID-mappedIDs.orig()));
			}
			return value;
		}

		public void mapIDs(int oldID,int newID, boolean seen) {
			_cache = new MappedIDPair(oldID,newID,seen);
			_idTree.add(trans(), _cache);
		}

		public void registerSeen(int id) {
			idMapping(id).seen(true);
		}

		private MappedIDPair idMapping(int id) {
			if(_cache.orig() == id){
				return _cache;
			}
			BTreeRange range=_idTree.search(trans(),new MappedIDPair(id,-1));
			Iterator4 pointers=range.pointers();
			if(!pointers.moveNext()) {
				return null;
			}
			_cache=(MappedIDPair)((BTreePointer)pointers.current()).key();
			return _cache;
		}
		
		public boolean hasSeen(int id) {
			MappedIDPair mappedIDs = idMapping(id);
			return mappedIDs!=null&&mappedIDs.seen();
		}
		
		public void clearSeen() {
			_idTree.traverseKeys(trans(), new Visitor4() {
				public void visit(Object obj) {
					((MappedIDPair)obj).seen(false);
				}
			});
		}

		public void close() {
			_mappingDb.close();
		}

		private Hashtable4 _classIDs=new Hashtable4();
		
		public void mapClassIDs(int oldID, int newID) {
			_classIDs.put(oldID,new Integer(newID));
		}
		
		private Transaction trans() {
			return _mappingDb.getSystemTransaction();
		}
	}