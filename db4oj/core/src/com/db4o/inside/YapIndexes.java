/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;


/**
 * 
 */
class YapIndexes {
    
    final YapFieldVersion i_fieldVersion;
    final YapFieldUUID i_fieldUUID;
    
    YapIndexes(ObjectContainerBase stream){
        i_fieldVersion = new YapFieldVersion(stream);
        i_fieldUUID = new YapFieldUUID(stream);
    }
}
