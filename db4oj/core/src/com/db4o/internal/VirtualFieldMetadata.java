/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.CorruptionException;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.ix.Indexable4;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.QConObject;
import com.db4o.internal.replication.*;
import com.db4o.internal.slots.Slot;


/**
 * TODO: refactor for symmetric inheritance - don't inherit from YapField and override,
 * instead extract an abstract superclass from YapField and let both YapField and this class implement
 * 
 * @exclude
 */
public abstract class VirtualFieldMetadata extends FieldMetadata {

    VirtualFieldMetadata() {
        super(null);
    }
    
    public abstract void addFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer a_writer, Slot oldSlot) throws FieldIndexException;
    
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
    
    public abstract void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate);
    
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

    public void instantiate(MarshallerFamily mf, ObjectReference a_yapObject, Object a_onObject, StatefulBuffer a_bytes)
        throws CorruptionException {
    	a_yapObject.produceVirtualAttributes();
        instantiate1(a_bytes.getTransaction(), a_yapObject, a_bytes);
    }

    abstract void instantiate1(Transaction a_trans, ObjectReference a_yapObject, Buffer a_bytes);
    
    public void loadHandler(ObjectContainerBase a_stream){
    	// do nothing
    }

    public final void marshall(
            ObjectReference a_yapObject, 
            Object a_object,
            MarshallerFamily mf, 
            StatefulBuffer a_bytes,
            Config4Class a_config, 
            boolean a_new) {
        
        Transaction trans = a_bytes.getTransaction();
        
        if(! trans.supportsVirtualFields()){
            marshallIgnore(a_bytes);
            return;
        }
        
        ObjectContainerBase stream = trans.stream();
        HandlerRegistry handlers = stream.i_handlers;
        boolean migrating = false;
        
        
        if (stream._replicationCallState != Const4.NONE) {
            if (stream._replicationCallState == Const4.OLD) {
                
                // old replication code 

                migrating = true;
                if (a_yapObject.virtualAttributes() == null) {
                    Object obj = a_yapObject.getObject();
                    ObjectReference migrateYapObject = null;
                    MigrationConnection mgc = handlers.i_migration;
                    if(mgc != null){
                        migrateYapObject = mgc.referenceFor(obj);
                        if(migrateYapObject == null){
                            migrateYapObject = mgc.peer(stream).referenceForObject(obj);
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

    abstract void marshall1(ObjectReference a_yapObject, StatefulBuffer a_bytes,
        boolean a_migrating, boolean a_new);
    
    abstract void marshallIgnore(Buffer writer);
    
    public void readVirtualAttribute(Transaction a_trans, Buffer a_reader, ObjectReference a_yapObject) {
        if(! a_trans.supportsVirtualFields()){
            a_reader.incrementOffset(linkLength());
            return;
        }
        instantiate1(a_trans, a_yapObject, a_reader);
    }
    
    public boolean isVirtual() {
        return true;
    }

    protected Object indexEntryFor(Object indexEntry) {
    	return indexEntry;
    }
    
    protected Indexable4 indexHandler(ObjectContainerBase stream) {
    	return (Indexable4)i_handler;
    }
}