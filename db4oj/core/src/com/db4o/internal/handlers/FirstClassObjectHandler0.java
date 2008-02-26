/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public class FirstClassObjectHandler0 extends FirstClassObjectHandler{

    public FirstClassObjectHandler0(ClassMetadata classMetadata) {
        super(classMetadata);
    }
    
    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
        return false;
    }

}
