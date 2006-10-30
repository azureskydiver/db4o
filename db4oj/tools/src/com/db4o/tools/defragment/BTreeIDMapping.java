/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

class BTreeIDMapping {
		private YapFile _mappingDb;
		private BTree _idTree;

		public BTreeIDMapping(String fileName) {
			_mappingDb = DefragContextImpl.freshYapFile(fileName);
			_idTree=new BTree(trans(),0,new MappedIDPairHandler(_mappingDb));
		}

		public Integer mappedID(int oldID,boolean lenient) {
			Integer classID=(Integer)_classIDs.get(oldID);
			if(classID!=null) {
				return classID;
			}
			BTreeRange range=_idTree.search(trans(),new MappedIDPair(oldID,0));
			MappedIDPair mappedIDs=null;
			Iterator4 pointers=range.pointers();
			if(pointers.moveNext()) {
				BTreePointer pointer=(BTreePointer)pointers.current();
				mappedIDs=(MappedIDPair)pointer.key();
			}
			if(mappedIDs!=null) {
				return new Integer(mappedIDs.mapped());
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
			_idTree.add(trans(), new MappedIDPair(oldID,newID,seen));
		}

		public void registerSeen(int id) {
			idMapping(id).seen(true);
		}

		private MappedIDPair idMapping(int id) {
			BTreeRange range=_idTree.search(trans(),new MappedIDPair(id,-1));
			Iterator4 pointers=range.pointers();
			if(!pointers.moveNext()) {
				return null;
			}
			MappedIDPair mappedIDs=(MappedIDPair)((BTreePointer)pointers.current()).key();
			return mappedIDs;
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