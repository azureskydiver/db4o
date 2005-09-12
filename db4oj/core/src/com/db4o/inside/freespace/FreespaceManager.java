/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;


public abstract class FreespaceManager {
    
    public abstract void free(int a_address, int a_length);
    
    public abstract int getSlot(int length);
    
    public abstract void read(int freeSlotsID);
    
    public abstract int write(boolean shuttingDown);
    
    
    

}
