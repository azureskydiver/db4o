/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class UUIDFieldMetadata extends VirtualFieldMetadata {
    
    private static final int LINK_LENGTH = Const4.LONG_LENGTH + Const4.ID_LENGTH;

    UUIDFieldMetadata(ObjectContainerBase stream) {
        super();
        setName(Const4.VIRTUAL_FIELD_PREFIX + "uuid");
        i_handler = new LongHandler(stream);
    }
    
    public void addFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer writer, Slot oldSlot) throws FieldIndexException {
        
        boolean isnew = (oldSlot == null);

        int offset = writer._offset;
        int db4oDatabaseIdentityID = writer.readInt();
        long uuid = writer.readLong();
        writer._offset = offset;
        
        LocalObjectContainer yf = (LocalObjectContainer)writer.getStream();
        
        if ((uuid == 0 || db4oDatabaseIdentityID == 0) && writer.getID() > 0
				&& !isnew) {
			DatabaseIdentityIDAndUUID identityAndUUID = readDatabaseIdentityIDAndUUID(
					yf, yapClass, oldSlot, false);
			db4oDatabaseIdentityID = identityAndUUID.databaseIdentityID;
			uuid = identityAndUUID.uuid;
		}
        
        if(db4oDatabaseIdentityID == 0){
            db4oDatabaseIdentityID = yf.identity().getID(writer.getTransaction());
        }
        
        if(uuid == 0){
            uuid = yf.generateTimeStampId();
        }
        
        writer.writeInt(db4oDatabaseIdentityID);
        writer.writeLong(uuid);
        
        if(isnew){
            addIndexEntry(writer, new Long(uuid));
        }
    }
    
    static class DatabaseIdentityIDAndUUID {
    	public int databaseIdentityID;
    	public long uuid;
		public DatabaseIdentityIDAndUUID(int databaseIdentityID_, long uuid_) {
			databaseIdentityID = databaseIdentityID_;
			uuid = uuid_;
		}
    }

   private DatabaseIdentityIDAndUUID readDatabaseIdentityIDAndUUID(ObjectContainerBase stream, ClassMetadata yapClass, Slot oldSlot, boolean checkClass) throws Db4oIOException {
        if(DTrace.enabled){
            DTrace.REREAD_OLD_UUID.logLength(oldSlot.address(), oldSlot.length());
        }
		Buffer reader = stream.bufferByAddress(oldSlot.address(), oldSlot.length());
		if(checkClass){
            ClassMetadata realClass = ClassMetadata.readClass(stream,reader);
            if(realClass != yapClass){
                return null;
            }
        }
		if (null == yapClass.findOffset(reader, this)) {
			return null;
		}
		return new DatabaseIdentityIDAndUUID(reader.readInt(), reader.readLong());
	}

    public void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate) {
        if(isUpdate){
            a_bytes.incrementOffset(linkLength());
            return;
        }
        a_bytes.incrementOffset(Const4.INT_LENGTH);
        long longPart = a_bytes.readLong();
        if(longPart > 0){
            ObjectContainerBase stream = a_bytes.getStream();
            if (stream.maintainsIndices()){
                removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), new Long(longPart));
            }
        }
    }
    
    public boolean hasIndex() {
    	return true;
    }
    
    public BTree getIndex(Transaction transaction) {
    	ensureIndex(transaction);
    	return super.getIndex(transaction);
    }
    
    protected void rebuildIndexForObject(LocalObjectContainer stream,
			ClassMetadata yapClass, int objectId) throws FieldIndexException {
		DatabaseIdentityIDAndUUID data = readDatabaseIdentityIDAndUUID(stream,
				yapClass, ((LocalTransaction) stream.systemTransaction())
						.getCurrentSlotOfID(objectId), true);
		if (null == data) {
			return;
		}
		addIndexEntry(stream.getLocalSystemTransaction(), objectId, new Long(
				data.uuid));
	}
    
	private void ensureIndex(Transaction transaction) {
		if (null == transaction) {
    		throw new ArgumentNullException();
    	}
    	if (null != super.getIndex(transaction)) {
    		return;    		
    	}
        LocalObjectContainer file = ((LocalObjectContainer)transaction.container());
        SystemData sd = file.systemData();
        if(sd == null){
            // too early, in new file, try again later.
            return;
        }
    	initIndex(transaction, sd.uuidIndexId());
    	if (sd.uuidIndexId() == 0) {
            sd.uuidIndexId(super.getIndex(transaction).getID());
            file.getFileHeader().writeVariablePart(file, 1);
    	}
	}

    void instantiate1(Transaction a_trans, ObjectReference a_yapObject, Buffer a_bytes) {
        int dbID = a_bytes.readInt();
        ObjectContainerBase stream = a_trans.container();
        stream.showInternalClasses(true);
        try {
	        Db4oDatabase db = (Db4oDatabase)stream.getByID2(a_trans, dbID);
	        if(db != null && db.i_signature == null){
	            stream.activate2(a_trans, db, 2);
	        }
	        VirtualAttributes va = a_yapObject.virtualAttributes();
	        va.i_database = db; 
	        va.i_uuid = a_bytes.readLong();
        } finally {
        	stream.showInternalClasses(false);
        }
    }

    public int linkLength() {
        return LINK_LENGTH;
    }
    
    void marshall1(ObjectReference a_yapObject, StatefulBuffer a_bytes, boolean a_migrating, boolean a_new) {
        ObjectContainerBase stream = a_bytes.getStream();
        Transaction trans = a_bytes.getTransaction();
        boolean indexEntry = a_new && stream.maintainsIndices();
        int dbID = 0;
		VirtualAttributes attr = a_yapObject.virtualAttributes();
		
		boolean linkToDatabase = ! a_migrating;
		if(attr != null && attr.i_database == null) {
			linkToDatabase = true;
		}
        if(linkToDatabase){
            Db4oDatabase db = stream.identity();
            if(db == null){
                // can happen on early classes like Metaxxx, no problem
                attr = null;
            }else{
    	        if (attr.i_database == null) {
    	            attr.i_database = db;
                    
                    // TODO: Should be check for ! client instead of instanceof
    	            if (stream instanceof LocalObjectContainer){
    					attr.i_uuid = stream.generateTimeStampId();
    	                indexEntry = true;
    	            }
    	        }
    	        db = attr.i_database;
    	        if(db != null) {
    	            dbID = db.getID(trans);
    	        }
            }
        }else{
            if(attr != null){
                dbID = attr.i_database.getID(trans);
            }
        }
        a_bytes.writeInt(dbID);
        if(attr != null){
	        a_bytes.writeLong(attr.i_uuid);
	        if(indexEntry){
	            addIndexEntry(a_bytes, new Long(attr.i_uuid));
	        }
        }else{
            a_bytes.writeLong(0);
        }
    }
    
    void marshallIgnore(Buffer writer) {
        writer.writeInt(0);
        writer.writeLong(0);
    }

	public final HardObjectReference getHardObjectReferenceBySignature(final Transaction transaction, final long longPart, final byte[] signature) {
		final BTreeRange range = search(transaction, new Long(longPart));		
		final Iterator4 keys = range.keys();
		while (keys.moveNext()) {
			final FieldIndexKey current = (FieldIndexKey) keys.current();
			final HardObjectReference hardRef = getHardObjectReferenceById(transaction, current.parentID(), signature);
			if (null != hardRef) {
				return hardRef;
			}
		}
		return HardObjectReference.INVALID;
	}

	protected final HardObjectReference getHardObjectReferenceById(Transaction transaction, int parentId, byte[] signature) {
		HardObjectReference hardRef = transaction.container().getHardObjectReferenceById(transaction, parentId);
        if (hardRef._reference == null) {
        	return null;
        }
        VirtualAttributes vad = hardRef._reference.virtualAttributes(transaction);
        if (!Arrays4.areEqual(signature, vad.i_database.i_signature)) {
            return null;
        }
        return hardRef;
	}
 
	public void defragField(MarshallerFamily mf, ReaderPair readers) {
		// database id
		readers.copyID(); 
		// uuid
		readers.incrementOffset(Const4.LONG_LENGTH);
	}
}