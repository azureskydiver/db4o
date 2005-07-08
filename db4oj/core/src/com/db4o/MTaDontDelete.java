/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.*;


/**
 * 
 */
class MTaDontDelete extends MsgD {
    
	final boolean processMessageAtServer(YapSocket in) {
        int classID = payLoad.readInt();
	    int id = payLoad.readInt();
	    Transaction trans = getTransaction();
	    YapStream stream = trans.i_stream;
	    synchronized (stream.i_lock) {
	        trans.dontDelete(classID, id);
	        return true;
	    }
	}

}
