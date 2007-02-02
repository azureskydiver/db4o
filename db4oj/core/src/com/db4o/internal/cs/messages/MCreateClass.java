/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.reflect.ReflectClass;

public final class MCreateClass extends MsgD {

	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
        ObjectContainerBase stream = stream();
        Transaction trans = stream.getSystemTransaction();
        try{
            ReflectClass claxx = trans.reflector().forName(readString());
            if (claxx != null) {
                synchronized (streamLock()) {
                    try {
                        ClassMetadata yapClass = stream.produceYapClass(claxx);
                        if (yapClass != null) {
                            stream.checkStillToSet();
                            yapClass.setStateDirty();
                            yapClass.write(trans);
                            trans.commit();
                            StatefulBuffer returnBytes = stream.readWriterByID(trans, yapClass.getID());
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
