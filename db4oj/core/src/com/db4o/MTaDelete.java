/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * 
 */
class MTaDelete extends MsgD {
    
	final boolean processMessageAtServer(YapSocket in) {
	    int id = payLoad.readInt();
	    int cascade = payLoad.readInt();
	    Transaction trans = getTransaction();
	    YapStream stream = trans.i_stream;
	    synchronized (stream.i_lock) {
	        Object[] arr = stream.getObjectAndYapObjectByID(trans, id);
	        trans.delete((YapObject)arr[1], arr[0],  cascade, true);
	        return true;
	    }
	}

}
