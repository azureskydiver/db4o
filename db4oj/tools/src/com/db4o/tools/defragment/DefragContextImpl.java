/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;

/**
 * @exclude
 */
public class DefragContextImpl implements DefragContext {	

	public static abstract class DbSelector {
		DbSelector() {
		}
		
		abstract YapFile db(DefragContextImpl context);
	}
	
	public final static DbSelector SOURCEDB=new DbSelector() {
		YapFile db(DefragContextImpl context) {
			return context._sourceDb;
		}
	};

	public final static DbSelector TARGETDB=new DbSelector() {
		YapFile db(DefragContextImpl context) {
			return context._targetDb;
		}
	};

	private static final long CLASSCOLLECTION_POINTER_ADDRESS = 2+2*YapConst.INT_LENGTH;
	
	private final YapFile _sourceDb;
	private final YapFile _targetDb;
	private final BTreeIDMapping _mapping;
	private Tree _seen;

	public DefragContextImpl(String sourceFileName,String targetFileName,String mappingFileName) {
		Db4o.configure().flushFileBuffers(false);
		Db4o.configure().readOnly(true);
		_sourceDb=(YapFile)Db4o.openFile(sourceFileName).ext();
		Db4o.configure().readOnly(false);
		_targetDb = freshYapFile(targetFileName);
		_mapping=new BTreeIDMapping(mappingFileName);
		_seen=null;
	}
	
	static YapFile freshYapFile(String fileName) {
		new File(fileName).delete();
		return (YapFile)Db4o.openFile(fileName).ext();
	}
	
	public int mappedID(int oldID,int defaultID) {
		Integer mapped=internalMappedID(oldID,false);
		return (mapped!=null ? mapped.intValue() : defaultID);
	}

	public int mappedID(int oldID) throws MappingNotFoundException {
		Integer mapped=internalMappedID(oldID,false);
		if(mapped==null) {
			throw new MappingNotFoundException(oldID);
		}
		return mapped.intValue();
	}

	public int mappedID(int id,boolean lenient) throws MappingNotFoundException {
		Integer mapped=internalMappedID(id,lenient);
		if(mapped==null) {
			throw new MappingNotFoundException(id);
		}
		return mapped.intValue();
	}

	private Integer internalMappedID(int oldID,boolean lenient) throws MappingNotFoundException {
		if(oldID==0) {
			return new Integer(0);
		}
		if(_sourceDb.handlers().isSystemHandler(oldID)) {
			return new Integer(oldID);
		}
		return _mapping.mappedID(oldID,lenient);
	}

	public void mapIDs(int oldID,int newID, boolean isClassID) {
		_mapping.mapIDs(oldID,newID);
		if(isClassID) {
			_mapping.mapClassIDs(oldID,newID);
		}
	}

	public void close() {
		_sourceDb.close();
		_targetDb.close();
		_mapping.close();
	}
	
	public YapReader readerByID(DbSelector selector,int id) {
		YapFile db = selector.db(this);
		return db.readReaderByID(db.getTransaction(), id);
	}

	public YapWriter sourceWriterByID(int id) {
		return _sourceDb.readWriterByID(_sourceDb.getTransaction(), id);
	}

	public YapReader sourceReaderByAddress(int address,int length) {
		return readerByAddress(SOURCEDB, address, length);
	}

	public YapReader targetReaderByAddress(int address,int length) {
		return readerByAddress(TARGETDB, address, length);
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

	public static void targetClassCollectionID(String file,int id) throws IOException {
		RandomAccessFile raf=new RandomAccessFile(file,"rw");
		try {
			YapReader reader=new YapReader(YapConst.INT_LENGTH);

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
		if(yapClass.hasIndex()&&!DefragContextImpl.COPY_INDICES) {
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

	public YapReader sourceReaderByID(int sourceID) {
		return readerByID(SOURCEDB,sourceID);
	}
	
	public BTree sourceUuidIndex() {
		if(sourceUuidIndexID()==0) {
			return null;
		}
		return _sourceDb.getFieldUUID().getIndex(systemTrans());
	}
	
	public void targetUuidIndexID(int id) {
		_targetDb.systemData().uuidIndexId(id);
	}

	public int sourceUuidIndexID() {
		return _sourceDb.systemData().uuidIndexId();
	}
	
	public YapClass yapClass(int id) {
		return _sourceDb.getYapClass(id);
	}
	
	public void registerSeen(int id) {
		//_mapping.registerSeen(id);
		_seen=Tree.add(_seen,new TreeInt(id));
	}

	public boolean hasSeen(int id) {
		//return _mapping.hasSeen(id);
		return _seen!=null&&_seen.find(new TreeInt(id))!=null;
	}
	
	public void clearSeen() {
		//_mapping.clearSeen();
		_seen=null;
	}
}