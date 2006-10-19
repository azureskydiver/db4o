/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;
import com.db4o.reflect.ReflectClass;

public final class MCreateClass extends MsgD {

	public final boolean processMessageAtServer(YapSocket sock) {
        YapStream stream = getStream();
        Transaction trans = stream.getSystemTransaction();
        YapWriter returnBytes = new YapWriter(trans, 0);
        try{
            ReflectClass claxx = trans.reflector().forName(readString());
            if (claxx != null) {
                synchronized (stream.i_lock) {
                    try {
                        YapClass yapClass = stream.getYapClass(claxx, true);
                        if (yapClass != null) {
                            stream.checkStillToSet();
                            yapClass.setStateDirty();
                            yapClass.write(trans);
                            trans.commit();
                            returnBytes = stream.readWriterByID(trans, yapClass.getID());
                            Msg.OBJECT_TO_CLIENT.getWriter(returnBytes).write(stream, sock);
                            return true;
    
                        } 
                        
                        // TODO: handling, if the class can't be created
                        
                    } catch (Throwable t) {
                        if (Deploy.debug) {
                            System.out.println("MCreateClass failed");
                        }
                    }
                }
            }
        }catch(Throwable th){
            if (Deploy.debug) {
                System.out.println("MCreateClass failed");
            }
        }
        Msg.FAILED.write(stream, sock);
        return true;
    }
}
