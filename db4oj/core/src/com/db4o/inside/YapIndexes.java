/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;


/**
 * 
 */
class YapIndexes {
    
    final VersionFieldMetadata i_fieldVersion;
    final UUIDFieldMetadata i_fieldUUID;
    
    YapIndexes(ObjectContainerBase stream){
        i_fieldVersion = new VersionFieldMetadata(stream);
        i_fieldUUID = new UUIDFieldMetadata(stream);
    }
}
