/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class MClassMetadataIdForName extends MsgD implements ServerSideMessage {
    
    public final boolean processAtServer() {
        String name = readString();
        ObjectContainerBase stream = stream();
        Transaction trans = stream.systemTransaction();
        boolean ok = false;
        try {
            synchronized (streamLock()) {
                int id = stream.classMetadataIdForName(name);
                MsgD msg = Msg.CLASS_ID.getWriterForInt(trans, id);
                write(msg);
                ok = true;
            }
        } catch (Db4oException e) {
            // TODO: send the exception to the client
        } finally {
            if (!ok) {
                write(Msg.FAILED);
            }
        }
        return true;
    }


}
