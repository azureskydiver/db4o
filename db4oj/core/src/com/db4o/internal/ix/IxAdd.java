/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.ix;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;

/**
 * An addition to a field index.
 */
public class IxAdd extends IxPatch {
    
    boolean _keepRemoved;

    public IxAdd(IndexTransaction a_ft, int a_parentID, Object a_value) {
        super(a_ft, a_parentID, a_value);
    }
    
    void beginMerge(){
        super.beginMerge();
        handler().prepareComparison( IxDeprecationHelper.comparableObject(handler(), trans(), _value));
    }
    
    public void visit(Object obj){
        ((Visitor4)obj).visit(new Integer(_parentID));
    }
    
    public void visit(Visitor4 visitor, int[] lowerAndUpperMatch){
        visitor.visit(new Integer(_parentID));
    }
    
    public void freespaceVisit(FreespaceVisitor visitor, int index){
        visitor.visit(_parentID, ((Integer)_value).intValue());
    }
    
    public int write(Indexable4 a_handler, StatefulBuffer a_writer) {
        a_handler.writeIndexEntry(a_writer, _value);
        a_writer.writeInt(_parentID);
        a_writer.writeForward();
        return 1;
    }
    
    public String toString(){
        String str = "IxAdd "  + _parentID + "\n " + IxDeprecationHelper.comparableObject(handler(), trans(), _value);
        return str;
    }

    public void visitAll(IntObjectVisitor visitor) {
        visitor.visit(_parentID, IxDeprecationHelper.comparableObject(handler(), trans(), _value));
    }

    public Object shallowClone() {
    	IxAdd add=new IxAdd(_fieldTransaction,_parentID,_value);
    	super.shallowCloneInternal(add);
    	add._keepRemoved=_keepRemoved;
    	return add;
    }
}
