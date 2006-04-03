/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;


/**
 * 
 */
class MObjectByUuid extends MsgD {
	public MObjectByUuid() {
		super();
	}

	public MObjectByUuid(MsgCloneMarker marker) {
		super(marker);
	}
    
	final boolean processMessageAtServer(YapSocket sock) {
		long uuid = readLong();
		byte[] signature = readBytes();
		int id = 0;
		YapStream stream = getStream();
		Transaction trans = getTransaction();
		synchronized (stream.i_lock) {
			try {
			    Object[] arr = trans.objectAndYapObjectBySignature(uuid, signature);
			    if(arr[1] != null){
			        YapObject yo = (YapObject)arr[1];
			        id = yo.getID();
			    }
			} catch (Exception e) {
			    if(Deploy.debug){
			        e.printStackTrace();
			    }
			}
		}
		Msg.OBJECT_BY_UUID.getWriterForInt(trans, id).write(stream,sock); 
		return true;
	}
    
    public Object shallowClone() {
    	return super.shallowCloneInternal(new MObjectByUuid(MsgCloneMarker.INSTANCE));
    }
}
