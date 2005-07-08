/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.ix.*;

/**
 * 
 */
class YapFieldUUID extends YapFieldVirtual {

    YapFieldUUID(YapStream stream) {
        super();
        i_name = YapConst.VIRTUAL_FIELD_PREFIX + "uuid";
        i_handler = new YLong(stream);
    }
    
    void addFieldIndex(YapWriter a_writer, boolean a_new) {

        int offset = a_writer._offset;
        int id = a_writer.readInt();
        long uuid = YLong.readLong(a_writer);
        a_writer._offset = offset;
        
        YapFile yf = (YapFile)a_writer.getStream();
        
        if(id == 0){
            a_writer.writeInt(yf.identity().getID(a_writer.getTransaction()));
        }else{
            a_writer.incrementOffset(YapConst.YAPINT_LENGTH);
        }
        
        if(uuid == 0){
            uuid = yf.i_bootRecord.newUUID();
        }
        YLong.writeLong(uuid, a_writer);
        
        if(a_new){
            addIndexEntry(a_writer, new Long(uuid));
        }
    }
    
    IxField getIndex(Transaction a_trans){
        YapFile stream = (YapFile)a_trans.i_stream;
        if(i_index == null){
            PBootRecord bootRecord = stream.i_bootRecord;
            i_index = new IxField(stream.getSystemTransaction(), this, bootRecord.getUUIDMetaIndex());
        }
        return i_index;
    }
    
    void instantiate1(Transaction a_trans, YapObject a_yapObject, YapReader a_bytes) {
        int dbID = a_bytes.readInt();
        YapStream stream = a_trans.i_stream;
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
        return YapConst.YAPLONG_LENGTH + YapConst.YAPID_LENGTH;
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
	        if (attr.i_database == null) {
	            attr.i_database = stream.identity();
	            if (stream instanceof YapFile
	                && ((YapFile) stream).i_bootRecord != null) {
	                PBootRecord bootRecord = ((YapFile) stream).i_bootRecord;
					attr.i_uuid = bootRecord.newUUID();
	                indexEntry = true;
	            }
	        }
	        Db4oDatabase db = attr.i_database;
	        if(db != null) {
	            dbID = db.getID(trans);
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


}