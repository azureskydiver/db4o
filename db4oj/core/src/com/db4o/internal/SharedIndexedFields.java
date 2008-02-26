/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class SharedIndexedFields {
    
    final VersionFieldMetadata _version;
    final UUIDFieldMetadata _uUID;
    
    public SharedIndexedFields(ObjectContainerBase stream){
        _version = new VersionFieldMetadata();
        _uUID = new UUIDFieldMetadata();
    }
}
