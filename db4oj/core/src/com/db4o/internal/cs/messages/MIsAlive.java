/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;


/**
 * @exclude
 */
public class MIsAlive extends Msg implements ServerSideMessage{ 

    public boolean processAtServer() {
        write(Msg.IS_ALIVE);
        return true;
    }
    

}
