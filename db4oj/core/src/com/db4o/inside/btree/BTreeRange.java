/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;


public interface BTreeRange {
    
    public void traverseKeys(Visitor4 visitor);

    public BTreePointer first();

}
