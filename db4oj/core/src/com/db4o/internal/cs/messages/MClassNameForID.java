/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.ClassMetadata;
import com.db4o.internal.cs.ServerMessageDispatcher;


/**
 * get the classname for an internal ID
 */
final class MClassNameForID extends MsgD{
    public final boolean processAtServer(ServerMessageDispatcher serverThread) {
        int id = _payLoad.readInt();
        String name = "";
        synchronized (streamLock()) {
			ClassMetadata yapClass = stream().getYapClass(id);
			if (yapClass != null) {
				name = yapClass.getName();
			}
		}
        serverThread.write(Msg.CLASS_NAME_FOR_ID.getWriterForString(transaction(), name));
        return true;
    }
}
