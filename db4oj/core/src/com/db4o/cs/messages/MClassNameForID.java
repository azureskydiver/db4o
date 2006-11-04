/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;


/**
 * get the classname for an internal ID
 */
final class MClassNameForID extends MsgD{
    public final boolean processAtServer(YapServerThread serverThread) {
        int id = _payLoad.readInt();
        String name = "";
        YapStream stream = getStream();
        // FIXME: CS access through method
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
        serverThread.write(Msg.CLASS_NAME_FOR_ID.getWriterForString(getTransaction(), name));
        return true;
    }
}
