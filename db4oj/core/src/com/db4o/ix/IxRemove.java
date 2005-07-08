/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * A node to represent an entry removed from an Index
 * @exclude
 */
public class IxRemove extends IxPatch {

    public IxRemove(IxFieldTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft, a_parentID, a_value);
        i_size = 0;
    }
    
    public int ownSize() {
        return 0;
    }

    public String toString() {
        String str = "IxRemove " + i_parentID + "\n " + handler().comparableObject(trans(), i_value);
        return str;
    }
    
    public void visit(Visitor4 visitor, int[] lowerAndUpperMatch){
        // do nothing
    }

    void write(YapDataType a_handler, YapWriter a_writer) {
        // do nothing
    }
}
