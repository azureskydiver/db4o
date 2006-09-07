/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.inside.ix.*;
import com.db4o.inside.marshall.*;
import com.db4o.inside.slots.*;

/**
 * 
 */
class YapFieldUUID extends YapFieldVirtual {
    
    private static final int LINK_LENGTH = YapConst.LONG_LENGTH + YapConst.ID_LENGTH;

    YapFieldUUID(YapStream stream) {
        super();
        i_name = YapConst.VIRTUAL_FIELD_PREFIX + "uuid";
        i_handler = new YLong(stream);
    }
    
    public void addFieldIndex(MarshallerFamily mf, YapClass yapClass, YapWriter writer, Slot oldSlot) {
        
        boolean isnew = (oldSlot == null);

        int offset = writer._offset;
        int db4oDatabaseIdentityID = writer.readInt();
        long uuid = YLong.readLong(writer);
        writer._offset = offset;
        
        YapFile yf = (YapFile)writer.getStream();
        
        if( (uuid == 0 || db4oDatabaseIdentityID == 0) && writer.getID() > 0 && ! isnew){
            YapReader reader = yf.readReaderByAddress(oldSlot.getAddress(), oldSlot.getLength());
            yapClass.findOffset(reader, this);
            db4oDatabaseIdentityID = reader.readInt();
            uuid = YLong.readLong(reader);
        }
        
        if(db4oDatabaseIdentityID == 0){
            db4oDatabaseIdentityID = yf.identity().getID(writer.getTransaction());
        }
        
        if(uuid == 0){
            uuid = yf.generateTimeStampId();
        }
        
        writer.writeInt(db4oDatabaseIdentityID);
        YLong.writeLong(uuid, writer);
        
        if(isnew){
            addIndexEntry(writer, new Long(uuid));
        }
    }

    public void delete(MarshallerFamily mf, YapWriter a_bytes, boolean isUpdate) {
        if(isUpdate){
            a_bytes.incrementOffset(linkLength());
            return;
        }
        a_bytes.incrementOffset(YapConst.INT_LENGTH);
        long longPart = YLong.readLong(a_bytes);
        if(longPart > 0){
            YapStream stream = a_bytes.getStream();
            if (stream.maintainsIndices()){
                removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), new Long(longPart));
            }
        }
    }
    
    Index4 getOldIndex(Transaction a_trans){
        if(_oldIndex != null){
            return _oldIndex;
        }
        YapFile stream = (YapFile)a_trans.stream();
        if(_oldIndex == null){
            MetaIndex metaIndex = stream.getUUIDMetaIndex();
            if(metaIndex == null){
                return null;
            }
            _oldIndex = new Index4(stream.getSystemTransaction(), getHandler(), metaIndex, false);
        }
        return _oldIndex;
    }
    
    boolean hasIndex() {
        return true;
    }
    
    void instantiate1(Transaction a_trans, YapObject a_yapObject, YapReader a_bytes) {
        int dbID = a_bytes.readInt();
        YapStream stream = a_trans.stream();
        stream.showInternalClasses(true);
        Db4oDatabase db = (Db4oDatabase)stream.getByID2(a_trans, dbID);
        if(db != null && db.i_signature == null){
            stream.activate2(a_trans, db, 2);
        }
        a_yapObject.i_virtualAttributes.i_database = db; 
        a_yapObject.i_virtualAttributes.i_uuid = YLong.readLong(a_bytes);
        stream.showInternalClasses(false);
    }

    public int linkLength() {
        return LINK_LENGTH;
    }
    
    void marshall1(YapObject a_yapObject, YapWriter a_bytes, boolean a_migrating, boolean a_new) {
        YapStream stream = a_bytes.getStream();
        Transaction trans = a_bytes.getTransaction();
        boolean indexEntry = a_new && stream.maintainsIndices();
        int dbID = 0;
		VirtualAttributes attr = a_yapObject.i_virtualAttributes;
		
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
    	            if (stream instanceof YapFile){
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
	        YLong.writeLong(attr.i_uuid, a_bytes);
	        if(indexEntry){
	            addIndexEntry(a_bytes, new Long(attr.i_uuid));
	        }
        }else{
            YLong.writeLong(0, a_bytes);
        }
    }
    
    void marshallIgnore(YapWriter writer) {
        writer.writeInt(0);
        YLong.writeLong(0, writer);
    }
 

}