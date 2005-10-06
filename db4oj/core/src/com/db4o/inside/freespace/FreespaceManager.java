/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;


public abstract class FreespaceManager {
    
    final YapFile     _file;
    
    public static final byte FM_RAM = 0;
    public static final byte FM_IX = 1;
    
    final int discardLimit(){
        return _file.i_config.i_discardFreeSpace;
    }
    
    final int blockSize(){
        return _file.blockSize();
    }
    
    public abstract void commit();
    
    public abstract void free(int a_address, int a_length);
    
    public abstract int getSlot(int length);
    
    public abstract void read(int freeSlotsID);
    
    public abstract int write(boolean shuttingDown);
    
    public abstract byte systemType();
    
    public abstract void start();
    
    public static FreespaceManager createNew(YapFile file, byte systemTypeByte){
        switch(systemTypeByte){
            case FM_RAM:
                return new FreespaceManagerRam(file);
            default:
                return new FreespaceManagerIx(file);
        }
    }
    
    FreespaceManager(YapFile file){
        _file = file;
    }
    

}
