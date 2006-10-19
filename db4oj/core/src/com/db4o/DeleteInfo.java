/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;


public class DeleteInfo extends TreeInt{
    
    public boolean _delete;
    int _cascade;
    public YapObject _reference;

    public DeleteInfo(int id, YapObject reference, boolean delete, int cascade) {
        super(id);
        _reference = reference;
        _delete = delete;
        _cascade = cascade;
    }
    public Object shallowClone() {
    	DeleteInfo deleteinfo= new DeleteInfo(0,_reference, _delete, _cascade);
    	return shallowCloneInternal(deleteinfo);
    }
    

}
