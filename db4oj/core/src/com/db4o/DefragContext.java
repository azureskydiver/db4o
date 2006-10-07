/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;

/**
 * @exclude
 */
public class DefragContext implements IDMapping {	

	private static class InMemoryIDMapping implements IDMapping {
		Hashtable4 _map=new Hashtable4(16);
		
		public void mapIDs(int oldID, int newID) {
			_map.put(oldID,new Integer(newID));
		}

		public int mappedID(int oldID) throws MappingNotFoundException {
			Integer newID=(Integer)_map.get(oldID);
			if(newID==null) {
				throw new MappingNotFoundException(oldID);
			}
			return newID.intValue();
		}

		public int mappedID(int oldID, int defaultID) {
			try {
				return mappedID(oldID);
			} catch (MappingNotFoundException e) {
				return defaultID;
			}
		}
	}
	
	private static class BTreeIDMapping implements IDMapping {
		private YapFile _mappingDb;
		private BTree _idBTree;

		public BTreeIDMapping(String fileName) {
			_mappingDb = freshYapFile(fileName);
			_idBTree=new BTree(_mappingDb.getTransaction(),0,new YInt(_mappingDb),new YInt(_mappingDb));
		}
		
		public int mappedID(int oldID) throws MappingNotFoundException {
			BTreeRange range=_idBTree.search(_mappingDb.getTransaction(), new Integer(oldID));
			Iterator4 pointers=range.pointers();
			if(!pointers.moveNext()) {
				throw new MappingNotFoundException(oldID);
			}
			BTreePointer pointer=(BTreePointer)pointers.current();
			Integer value = (Integer)pointer.value();
			if (value == null)
				throw new MappingNotFoundException(oldID);
			
			return value.intValue();
		}

		public int mappedID(int oldID, int defaultID) {
			return 0;
		}
		
		public void mapIDs(int oldID,int newID) {
			_idBTree.add(_mappingDb.getTransaction(), new Integer(oldID),new Integer(newID));
		}
		
		public void close() {
			_mappingDb.close();
		}
	}
	
	public static abstract class DbSelector {
		private DbSelector() {
		}
		
		abstract YapFile db(DefragContext context);
	}
	
	public final static DbSelector SOURCEDB=new DbSelector() {
		YapFile db(DefragContext context) {
			return context._sourceDb;
		}
	};

	public final static DbSelector TARGETDB=new DbSelector() {
		YapFile db(DefragContext context) {
			return context._targetDb;
		}
	};

	private static final long CLASSCOLLECTION_POINTER_ADDRESS = 2+2*YapConst.INT_LENGTH;
	
	private final YapFile _sourceDb;
	private final YapFile _targetDb;
	private final IDMapping _mapping;

	public DefragContext(String sourceFileName,String targetFileName,String mappingFileName) {
		Db4o.configure().flushFileBuffers(false);
		Db4o.configure().readOnly(true);
		_sourceDb=(YapFile)Db4o.openFile(sourceFileName).ext();
		Db4o.configure().readOnly(false);
		_targetDb = freshYapFile(targetFileName);
		//_mapping=new BTreeIDMapping(mappingFileName);
		_mapping=new InMemoryIDMapping();
	}
	
	private static YapFile freshYapFile(String fileName) {
		new File(fileName).delete();
		return (YapFile)Db4o.openFile(fileName).ext();
	}
	
	public int mappedID(int oldID,int defaultID) {
		try {
			return mappedID(oldID);
		} catch (MappingNotFoundException e) {
			return defaultID;
		}
	}
	
	public int mappedID(int oldID) throws MappingNotFoundException {
		if(oldID==0) {
			return 0;
		}
		if(_sourceDb.handlers().isSystemHandler(oldID)) {
			return oldID;
		}
		return _mapping.mappedID(oldID);
	}

	public void mapIDs(int oldID,int newID) {
		_mapping.mapIDs(oldID,newID);
	}

	public void close() {
		_sourceDb.close();
		_targetDb.close();
	}
	
	public YapReader readerByID(DbSelector selector,int id) {
		YapFile db = selector.db(this);
		return db.readReaderByID(db.getTransaction(), id);
	}

	public YapReader readerByAddress(DbSelector selector,int address,int length) {
		return selector.db(this).readReaderByAddress(address,length);
	}

	public YapWriter targetWriterByAddress(int address,int length) {
		return _targetDb.readWriterByAddress(_targetDb.getTransaction(),address,length);
	}
	
	public int allocateTargetSlot(int length) {
		return _targetDb.getSlot(length);
	}

	public void targetWriteBytes(ReaderPair readers,int address) {
		readers.write(_targetDb,address);
	}

	public void targetWriteBytes(YapReader reader,int address) {
		_targetDb.writeBytes(reader,address,0);
	}

	public StoredClass[] storedClasses(DbSelector selector) {
		YapFile db = selector.db(this);
		db.showInternalClasses(true);
		StoredClass[] classes=db.storedClasses();
		return classes;
	}
	
	public YapStringIO stringIO() {
		return _sourceDb.stringIO();
	}
	
	public void targetCommit() {
		_targetDb.commit();
	}
	
	public TypeHandler4 sourceHandler(int id) {
		return _sourceDb.handlerByID(id);
	}
	
	public int sourceClassCollectionID() {
		return _sourceDb.classCollection().getID();
	}

	public int bootRecordClassID(DbSelector selector) {
		return classID(selector,PBootRecord.class);
	}

	public int p1ObjectClassID(DbSelector selector) {
		return classID(selector,P1Object.class);
	}

	private int classID(DbSelector selector,Class clazz) {
		YapFile db=selector.db(this);
		db.showInternalClasses(true);
		int id=db.getYapClass(db.reflector().forClass(clazz), true).getID();
		db.showInternalClasses(false);
		return id;
	}

	public static void targetClassCollectionID(String file,int id) throws IOException {
		RandomAccessFile raf=new RandomAccessFile(file,"rw");
		try {
			YapReader reader=new YapReader(YapConst.INT_LENGTH);

			raf.seek(CLASSCOLLECTION_POINTER_ADDRESS);			
			raf.read(reader._buffer);
			int oldID=reader.readInt();
			
			raf.seek(CLASSCOLLECTION_POINTER_ADDRESS);			
			reader._offset=0;
			reader.writeInt(id);
			raf.write(reader._buffer);
		}
		finally {
			raf.close();
		}
	}

	private Hashtable4 _classIndices=new Hashtable4(16);

	public static final boolean COPY_INDICES=true;

	public void addClassID(YapClass yapClass,long id) {
		if(yapClass.hasIndex()&&!DefragContext.COPY_INDICES) {
			ClassIndexStrategy classIndex = classIndex(yapClass);
			classIndex.add(_targetDb.getTransaction(), (int)id);
		}
	}

	public int classIndexID(YapClass yapClass) {
		return classIndex(yapClass).id();
	}

	public void traverseAll(YapClass yapClass,Visitor4 command) {
		yapClass.index().traverseAll(_sourceDb.getTransaction(), command);
	}
	
	public void traverseAllIndexSlots(YapClass yapClass,Visitor4 command) {
		Iterator4 slotIDIter=yapClass.index().allSlotIDs(_sourceDb.getTransaction());
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}

	public void traverseAllIndexSlots(BTree btree,Visitor4 command) {
		Iterator4 slotIDIter=btree.allNodeIds(_sourceDb.getTransaction());
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}

	public int databaseIdentityID(DbSelector selector) {
		YapFile db = selector.db(this);
		return db.identity().getID(db.getSystemTransaction());
	}
	
	private ClassIndexStrategy classIndex(YapClass yapClass) {
		ClassIndexStrategy classIndex=(ClassIndexStrategy)_classIndices.get(yapClass);
		if(classIndex==null) {
			classIndex=new BTreeClassIndexStrategy(yapClass);
			_classIndices.put(yapClass,classIndex);
			classIndex.initialize(_targetDb);
		}
		return classIndex;
	}

	public Transaction systemTrans() {
		return _sourceDb.getSystemTransaction();
	}

	public void copyIdentity() {
		_targetDb.setIdentity(_sourceDb.identity());
	}

	public void targetClassCollectionID(int newClassCollectionID) {
		_targetDb.systemData().classCollectionID(newClassCollectionID);
	}
}