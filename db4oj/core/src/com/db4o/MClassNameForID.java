/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;


/**
 * get the classname for an internal ID
 */
final class MClassNameForID extends MsgD{
	public MClassNameForID() {
		super();
	}

	public MClassNameForID(MsgCloneMarker marker) {
		super(marker);
	}

    final boolean processMessageAtServer(YapSocket sock) {
        int id = _payLoad.readInt();
        String name = "";
        YapStream stream = getStream();
        synchronized (stream.i_lock) {
            try {
                YapClass yapClass = stream.getYapClass(id);
                if(yapClass != null){
                    name = yapClass.getName();
                }
                
            } catch (Throwable t) {
                if (Deploy.debug) {
                    System.out.println("MClassNameForID failed");
                }
            }
        }
        Msg.CLASS_NAME_FOR_ID.getWriterForString(getTransaction(), name).write(stream, sock);
        return true;
    }
    
    public Object shallowClone() {
    	return super.shallowCloneInternal(new MClassNameForID(MsgCloneMarker.INSTANCE));
    }
}
