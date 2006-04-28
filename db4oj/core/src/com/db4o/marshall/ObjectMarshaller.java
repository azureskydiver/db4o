/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;

public interface ObjectMarshaller {
    
    boolean findOffset(YapClass yc, YapReader a_bytes, YapField a_field);

    void instantiateFields(YapClass yc, YapObject a_yapObject, Object a_onObject,
        YapWriter a_bytes);

    YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth);

    void marshallUpdate(Transaction a_trans, YapClass yapClass, int a_id,
        int a_updateDepth, YapObject a_yapObject, Object a_object);

}