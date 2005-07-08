/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.*;
import com.db4o.reflect.*;

final class MCreateClass extends MsgD {
    final boolean processMessageAtServer(YapSocket sock) {
        ReflectClass claxx = null;
        YapStream stream = getStream();
        Transaction trans = stream.getSystemTransaction();
        YapWriter returnBytes = new YapWriter(trans, 0);
            claxx = trans.reflector().forName(this.readString());
        if (claxx != null) {
            synchronized (stream.i_lock) {
                try {
                    YapClass yapClass = stream.getYapClass(claxx, true);
                    if (yapClass != null) {
                        stream.checkStillToSet();
                        yapClass.setStateDirty();
                        yapClass.write(stream, trans);
                        trans.commit();
                        returnBytes = stream.readWriterByID(trans, yapClass.getID());
                    } else {
                        // TODO: handling, if the class can't be created 
                    }
                } catch (Throwable t) {
                    if (Deploy.debug) {
                        System.out.println("MsgD.CreateClass failed");
                    }
                }
            }
        }
        //		TODO: now what is written here, if the class can't be created?
        Msg.OBJECT_TO_CLIENT.getWriter(returnBytes).write(stream, sock);
        return true;
    }
}
