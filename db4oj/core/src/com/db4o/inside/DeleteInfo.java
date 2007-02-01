/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;


/**
 * @exclude
 */
public class DeleteInfo extends TreeInt{
    
    int _cascade;
    public YapObject _reference;

    public DeleteInfo(int id, YapObject reference, int cascade) {
        super(id);
        _reference = reference;
        _cascade = cascade;
    }
    public Object shallowClone() {
    	DeleteInfo deleteinfo= new DeleteInfo(0,_reference,  _cascade);
    	return shallowCloneInternal(deleteinfo);
    }
    

}
