/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.reflect.ReflectClass;

public final class MCreateClass extends MsgD {

	public final boolean processAtServer(YapServerThread serverThread) {
        YapStream stream = stream();
        Transaction trans = stream.getSystemTransaction();
        YapWriter returnBytes = new YapWriter(trans, 0);
        try{
            ReflectClass claxx = trans.reflector().forName(readString());
            if (claxx != null) {
                synchronized (streamLock()) {
                    try {
                        YapClass yapClass = stream.produceYapClass(claxx);
                        if (yapClass != null) {
                            stream.checkStillToSet();
                            yapClass.setStateDirty();
                            yapClass.write(trans);
                            trans.commit();
                            returnBytes = stream.readWriterByID(trans, yapClass.getID());
                            serverThread.write(Msg.OBJECT_TO_CLIENT.getWriter(returnBytes));
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
        serverThread.write(Msg.FAILED);
        return true;
    }
}
