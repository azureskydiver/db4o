/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.query.*;

/**
 * 
 */
class ReplicationImpl implements Db4oReplication, Db4oReplicationConflict {

    final YapStream   i_source;
    final Transaction i_sourceTrans;

    YapStream         i_destination;
    Transaction       i_destinationTrans;

    Db4oCallback      i_conflictHandler;

    ReplicationRecord i_record;

    Db4oDatabase      i_sourceDatabaseInDestination;

    private Object    i_destinationObject;
    private Object    i_sourceObject;
    
    private YapObject i_sourceYapObject;
    
    private int i_direction; 
    
    private static final int IGNORE = 0;
    private static final int TO_DESTINATION = -1;
    private static final int TO_SOURCE = 1;


    ReplicationImpl(YapStream a_source, ObjectContainer a_destination) {
        i_source = a_source;
        i_sourceTrans = a_source.checkTransaction(null);

        if (a_destination != null) {

            i_destination = (YapStream) a_destination;
            i_destinationTrans = i_destination.checkTransaction(null);

            i_source.i_handlers.i_replication = this;
            i_source.i_migrateFrom = i_destination;

            i_destination.i_handlers.i_replication = this;
            i_destination.i_migrateFrom = i_source;

            i_sourceDatabaseInDestination = i_destination.i_handlers
                .ensureDb4oDatabase(i_destinationTrans, i_source.identity());
            ObjectSet objectSet = queryForReplicationRecord();
            if (objectSet.hasNext()) {
                i_record = (ReplicationRecord) objectSet.next();
            } else {
                i_record = new ReplicationRecord();
                i_record.i_source = i_sourceDatabaseInDestination;
                i_record.i_target = i_destination.identity();
            }
        }
    }

    public void commit() {
        i_source.commit();
        i_destination.commit();

        long i_sourceVersion = i_source.currentVersion() - 1;
        long i_destinationVersion = i_destination.currentVersion() - 1;

        i_record.i_version = i_destinationVersion;

        if (i_sourceVersion > i_destinationVersion) {
            i_record.i_version = i_sourceVersion;
            i_destination.raiseVersion(i_record.i_version);
        } else if (i_destinationVersion > i_sourceVersion) {
            i_source.raiseVersion(i_record.i_version);
            i_source.commit();
        }
        i_destination.showInternalClasses(true);
        i_destination.set(i_record);
        i_destination.commit();
        i_destination.showInternalClasses(false);

        endReplication();
    }

    public void rollback() {
        if (i_destination != null) {
            i_destination.rollback();
        }
        i_source.rollback();
        endReplication();
    }

    private void endReplication() {
        i_source.i_migrateFrom = null;
        i_source.i_handlers.i_replication = null;
        i_destination.i_migrateFrom = null;
        i_destination.i_handlers.i_replication = null;
    }

    private ObjectSet queryForReplicationRecord() {
        i_destination.showInternalClasses(true);
        Query q = i_destination.querySharpenBug();
        q.constrain(YapConst.CLASS_REPLICATIONRECORD);
        q.descend("i_source").constrain(i_sourceDatabaseInDestination).identity();
        q.descend("i_target").constrain(i_destination.identity()).identity();
        ObjectSet objectSet = q.execute();
        i_destination.showInternalClasses(false);
        return objectSet;
    }

    public void setConflictHandler(Db4oCallback callback) {
        i_conflictHandler = callback;
    }

    boolean toDestination(Object a_sourceObject) {
        synchronized(i_source.i_lock){
            i_sourceObject = a_sourceObject;
	        i_sourceYapObject = i_source.getYapObject(a_sourceObject);
	        if (i_sourceYapObject != null) {
	            VirtualAttributes vas = i_sourceYapObject.virtualAttributes(i_sourceTrans);
	            if (vas != null) {
	                
	                Object[] arr = i_destinationTrans.objectAndYapObjectBySignature(vas.i_uuid, vas.i_database.i_signature); 
	                if (arr[0] != null) {
	                    YapObject yod = (YapObject) arr[1];
	                    i_destinationObject = arr[0];
	                    VirtualAttributes vad = yod
	                        .virtualAttributes(i_destinationTrans);
	                    if (vas.i_version <= i_record.i_version
	                        && vad.i_version <= i_record.i_version) {
	                        i_destination.bind2(yod, i_sourceObject);
	                        return true;
	                    }
	                    
	                    if (vas.i_version > i_record.i_version
	                        && vad.i_version > i_record.i_version) {
	                        
	                        if(i_conflictHandler == null){
	                            i_destination.bind2(yod, i_sourceObject);
	                            return true;
	                        }
	                        
	                        i_direction = IGNORE;
	                        i_conflictHandler.callback(this);
	                        
	                        if(i_direction == IGNORE){
	                            i_destination.bind2(yod, i_sourceObject);
	                            return true;
	                        }
	                    }else{
	                        i_direction = TO_DESTINATION;
	                        if(vad.i_version > i_record.i_version){
	                            i_direction = TO_SOURCE;
	                        }
	                    }
	                    
	                    if(i_direction == TO_SOURCE){
	                        if(! yod.isActive()){
	                            yod.activate(i_destinationTrans, i_destinationObject, 1, false);
	                        }
	                        i_source.bind2(i_sourceYapObject, i_destinationObject);
	                        i_source.setNoReplication(i_sourceTrans, i_destinationObject, 1, true);
	                    }else{
	                        if( ! i_sourceYapObject.isActive()){
	                            i_sourceYapObject.activate(i_sourceTrans, i_sourceObject, 1, false);
	                        }
		                    i_destination.bind2(yod, i_sourceObject);
		                    i_destination.setNoReplication(i_destinationTrans, i_sourceObject, 1, true);
	                    }
	                    
	                    return true;
	                }
	            }
	        }
	        return false;
        }
    }
    
    void destinationOnNew(YapObject a_yod){
        if(i_sourceYapObject != null){
            VirtualAttributes vas = i_sourceYapObject.virtualAttributes(i_sourceTrans);
            a_yod.i_virtualAttributes = new VirtualAttributes();
            VirtualAttributes vad = a_yod.i_virtualAttributes;
            vad.i_uuid = vas.i_uuid;
            vad.i_version = vas.i_version;
            vad.i_database = vas.i_database;
        }
    }

    /* Db4oReplicationConflict interface */
    /* --------------------------------- */

    public ObjectContainer destination() {
        return i_destination;
    }

    public Object destinationObject() {
        return i_destinationObject;
    }

    public ObjectContainer source() {
        return i_source;
    }

    public Object sourceObject() {
        return i_sourceObject;
    }

    public void useSource() {
        i_direction = TO_DESTINATION;
    }

    public void useDestination() {
        i_direction = TO_SOURCE;
    }

}