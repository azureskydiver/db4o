/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.ix;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;

/**
 * A node to represent an entry removed from an Index
 */
public class IxRemove extends IxPatch {

    public IxRemove(IndexTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft, a_parentID, a_value);
        _size = 0;
    }
    
    public int ownSize() {
        return 0;
    }

    public String toString() {
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
        String str = "IxRemove " + _parentID + "\n " + handler().comparableObject(trans(), _value);
        return str;
    }
    
    public void freespaceVisit(FreespaceVisitor visitor, int index){
        // do nothing
    }
    
    public void visit(Object obj){
        // do nothing
    }
    
    public void visit(Visitor4 visitor, int[] lowerAndUpperMatch){
        // do nothing
    }

    public int write(Indexable4 a_handler, StatefulBuffer a_writer) {
        // do nothing
        return 0;
    }

    public void visitAll(IntObjectVisitor visitor) {
        // do nothing
    }
    
    public Object shallowClone() {
    	IxRemove remove=new IxRemove(_fieldTransaction,_parentID,_value);
    	super.shallowCloneInternal(remove);
    	return remove;
    }
}
