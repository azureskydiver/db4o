/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


/**
 * get the classname for an internal ID
 */
final class MClassNameForID extends MsgD{
    final boolean processMessageAtServer(YapSocket sock) {
        int id = payLoad.readInt();
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
}
