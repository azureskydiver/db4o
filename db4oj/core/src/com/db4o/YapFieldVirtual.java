/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * 
 */
abstract class YapFieldVirtual extends YapField {

    YapFieldVirtual() {
        super(null);
    }
    
    void addFieldIndex(YapWriter a_writer, boolean a_new) {
        a_writer.incrementOffset(linkLength());
    }
    
    public void appendEmbedded2(YapWriter a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }
    
    public boolean alive() {
        return true;
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
    
    void delete(YapWriter a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }
    
    Object getOrCreate(Transaction a_trans, Object a_OnObject) {
        // This is the first part of marshalling
        // Virtual fields do it all in #marshall() so it's fine to return null here
        return null;
    }

    int ownLength(YapStream a_stream) {
        return a_stream.stringIO().shortLength(i_name);
    }

    void initIndex(YapStream a_stream, MetaIndex a_metaIndex) {
        if (i_index == null) {
            i_index = new IxField(a_stream.getSystemTransaction(), this,
                a_metaIndex);
        }
    }

    void instantiate(YapObject a_yapObject, Object a_onObject, YapWriter a_bytes)
        throws CorruptionException {
        if (a_yapObject.i_virtualAttributes == null) {
            a_yapObject.i_virtualAttributes = new VirtualAttributes();
        }
        instantiate1(a_bytes.getTransaction(), a_yapObject, a_bytes);
    }

    abstract void instantiate1(Transaction a_trans, YapObject a_yapObject, YapReader a_bytes);
    
    void loadHandler(YapStream a_stream){
    	// do nothing
    }

    void marshall(YapObject a_yapObject, Object a_object, YapWriter a_bytes,
        Config4Class a_config, boolean a_new) {
        YapStream stream = a_bytes.i_trans.i_stream;
        boolean migrating = false;
        if (stream instanceof YapFile) {
            if (stream.i_migrateFrom != null) {
                migrating = true;
	            if (a_yapObject.i_virtualAttributes == null) {
                    Object obj = a_yapObject.getObject();
                    YapObject migrateYapObject = null;
                    if(stream.i_handlers.i_migration != null){
                        migrateYapObject = stream.i_handlers.i_migration.referenceFor(obj);
                    }
                     if(migrateYapObject == null){
                         migrateYapObject = stream.i_migrateFrom.getYapObject(obj);
                     }
                    
	                if (migrateYapObject != null
	                    && migrateYapObject.i_virtualAttributes != null
	                    && migrateYapObject.i_virtualAttributes.i_database != null) {
	                    migrating = true;
	                    a_yapObject.i_virtualAttributes = migrateYapObject.i_virtualAttributes
	                        .shallowClone();
                        a_bytes.getTransaction().ensureDb4oDatabase(migrateYapObject.i_virtualAttributes.i_database);
	                }
	            }
	        }
	        if (a_yapObject.i_virtualAttributes == null) {
	            a_yapObject.i_virtualAttributes = new VirtualAttributes();
	            migrating = false;
	        }
        }else{
            migrating = true;
        }
	    marshall1(a_yapObject, a_bytes, migrating, a_new);
    }

    abstract void marshall1(YapObject a_yapObject, YapWriter a_bytes,
        boolean a_migrating, boolean a_new);
    
    public void readVirtualAttribute(Transaction a_trans, YapReader a_reader, YapObject a_yapObject) {
        instantiate1(a_trans, a_yapObject, a_reader);
    }

    void writeThis(YapWriter a_writer, YapClass a_onClass) {
        a_writer.writeShortString(i_name);
    }
}