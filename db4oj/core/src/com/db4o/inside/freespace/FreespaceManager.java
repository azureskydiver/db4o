/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.freespace;

import com.db4o.*;
import com.db4o.inside.*;

public abstract class FreespaceManager {
    
    final LocalObjectContainer     _file;

    public static final byte FM_DEFAULT = 0;
    public static final byte FM_LEGACY_RAM = 1;
    public static final byte FM_RAM = 2;
    public static final byte FM_IX = 3;
    public static final byte FM_DEBUG = 4;
    
    private static final int INTS_IN_SLOT = 12;
    
    public FreespaceManager(LocalObjectContainer file){
        _file = file;
    }

    public static byte checkType(byte systemType){
        if(systemType == FM_DEFAULT){
            return FM_RAM;    
        }
        return systemType;
    }
    
    public static FreespaceManager createNew(LocalObjectContainer file){
        return createNew(file, file.systemData().freespaceSystem());
    }
    
	public abstract void onNew(LocalObjectContainer file);
    
    public static FreespaceManager createNew(LocalObjectContainer file, byte systemType){
        systemType = checkType(systemType);
        switch(systemType){
        	case FM_IX:
        		return new FreespaceManagerIx(file);
            default:
                return new FreespaceManagerRam(file);
                
        }
    }
    
    public static int initSlot(LocalObjectContainer file){
        int address = file.getSlot(slotLength());
        slotEntryToZeroes(file, address);
        return address;
    }
    
    static void slotEntryToZeroes(LocalObjectContainer file, int address){
        StatefulBuffer writer = new StatefulBuffer(file.getSystemTransaction(), address, slotLength());
        for (int i = 0; i < INTS_IN_SLOT; i++) {
            writer.writeInt(0);
        }
        if (Debug.xbytes) {
            writer.setID(YapConst.IGNORE_ID);  // no XBytes check
        }
        writer.writeEncrypt();
    }
    
    
    final static int slotLength(){
        return YapConst.INT_LENGTH * INTS_IN_SLOT;
    }
    
    public abstract void beginCommit();
    
    final int blockSize(){
        return _file.blockSize();
    }
    
    public abstract void debug();
    
    final int discardLimit(){
        return _file.configImpl().discardFreeSpace();
    }
    
    public abstract void endCommit();
    
    public abstract int entryCount();

    public abstract void free(int a_address, int a_length);
    
    public abstract int freeSize();
    
    public abstract void freeSelf();
    
    public abstract int getSlot(int length);
    
    public abstract void migrate(FreespaceManager newFM);
    
    public abstract void read(int freeSlotsID);
    
    public abstract void start(int slotAddress);
    
    public abstract byte systemType();
    
    public abstract int write(boolean shuttingDown);

    public boolean requiresMigration(byte configuredSystem, byte readSystem) {
        return (configuredSystem != 0 || readSystem == FM_LEGACY_RAM ) && (systemType() != configuredSystem);
    }

    public static void migrate(FreespaceManager oldFM, FreespaceManager newFM) {
    	oldFM.migrate(newFM);
    	oldFM.freeSelf();
    	newFM.beginCommit();
    	newFM.endCommit();
    }
    
}
