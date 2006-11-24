/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;
import com.db4o.inside.replication.*;
import com.db4o.inside.slots.*;

/**
 * @exclude
 */
public abstract class YapFieldVirtual extends YapField {

    YapFieldVirtual() {
        super(null);
    }
    
    public abstract void addFieldIndex(MarshallerFamily mf, YapClass yapClass, YapWriter a_writer, Slot oldSlot);
    
    public boolean alive() {
        return true;
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, Object obj){
        header.addBaseLength(linkLength());
    }
    
    boolean canAddToQuery(String fieldName){
        return fieldName.equals(getName()); 
    }
    
    public boolean canUseNullBitmap(){
        return false;
    }
    
    void collectConstraints(Transaction a_trans, QConObject a_parent,
        Object a_template, Visitor4 a_visitor) {
        
        // QBE constraint collection call
        // There isn't anything useful to do here, since virtual fields
        // are not on the actual object.
        
    }
    
    void deactivate(Transaction a_trans, Object a_onObject, int a_depth) {
        // do nothing
    }
    
    public abstract void delete(MarshallerFamily mf, YapWriter a_bytes, boolean isUpdate);
    
    public Object getOrCreate(Transaction a_trans, Object a_OnObject) {
        // This is the first part of marshalling
        // Virtual fields do it all in #marshall() so it's fine to return null here
        return null;
    }
    
    public boolean needsArrayAndPrimitiveInfo(){
        return false;
    }

    public boolean needsHandlerId(){
        return false;
    }

    public void instantiate(MarshallerFamily mf, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes)
        throws CorruptionException {
    	a_yapObject.produceVirtualAttributes();
        instantiate1(a_bytes.getTransaction(), a_yapObject, a_bytes);
    }

    abstract void instantiate1(Transaction a_trans, YapObject a_yapObject, YapReader a_bytes);
    
    public void loadHandler(YapStream a_stream){
    	// do nothing
    }

    public final void marshall(
            YapObject a_yapObject, 
            Object a_object,
            MarshallerFamily mf, 
            YapWriter a_bytes,
            Config4Class a_config, 
            boolean a_new) {
        
        Transaction trans = a_bytes.i_trans;
        
        if(! trans.supportsVirtualFields()){
            marshallIgnore(a_bytes);
            return;
        }
        
        YapStream stream = trans.stream();
        YapHandlers handlers = stream.i_handlers;
        boolean migrating = false;
        
        
        if (stream._replicationCallState != YapConst.NONE) {
            if (stream._replicationCallState == YapConst.OLD) {
                
                // old replication code 

                migrating = true;
                if (a_yapObject.virtualAttributes() == null) {
                    Object obj = a_yapObject.getObject();
                    YapObject migrateYapObject = null;
                    MigrationConnection mgc = handlers.i_migration;
                    if(mgc != null){
                        migrateYapObject = mgc.referenceFor(obj);
                        if(migrateYapObject == null){
                            migrateYapObject = mgc.peer(stream).getYapObject(obj);
                        }
                    }
                    if (migrateYapObject != null){
                    	VirtualAttributes migrateAttributes = migrateYapObject.virtualAttributes();
                    	if(migrateAttributes != null && migrateAttributes.i_database != null){
	                        migrating = true;
	                        a_yapObject.setVirtualAttributes((VirtualAttributes)migrateAttributes.shallowClone());
                            migrateAttributes.i_database.bind(trans);
                    	}
                    }
                }
            }else {
                
                // new dRS replication
                
                Db4oReplicationReferenceProvider provider = handlers._replicationReferenceProvider;
                Object parentObject = a_yapObject.getObject();
                Db4oReplicationReference ref = provider.referenceFor(parentObject); 
                if(ref != null){
                    migrating = true;
                    VirtualAttributes va = a_yapObject.produceVirtualAttributes();
                    va.i_version = ref.version();
                    va.i_uuid = ref.longPart();
                    va.i_database = ref.signaturePart();
                }
            }
        }
        
        if (a_yapObject.virtualAttributes() == null) {
        	a_yapObject.produceVirtualAttributes();
            migrating = false;
        }
	    marshall1(a_yapObject, a_bytes, migrating, a_new);
    }

    abstract void marshall1(YapObject a_yapObject, YapWriter a_bytes,
        boolean a_migrating, boolean a_new);
    
    abstract void marshallIgnore(YapReader writer);
    
    public void readVirtualAttribute(Transaction a_trans, YapReader a_reader, YapObject a_yapObject) {
        if(! a_trans.supportsVirtualFields()){
            a_reader.incrementOffset(linkLength());
            return;
        }
        instantiate1(a_trans, a_yapObject, a_reader);
    }
    
    public boolean isVirtual() {
        return true;
    }

}