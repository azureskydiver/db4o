/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public abstract class AbstractFreespaceManager implements FreespaceManager {
    
    final LocalObjectContainer     _file;

    public static final byte FM_DEFAULT = 0;
    public static final byte FM_LEGACY_RAM = 1;
    public static final byte FM_RAM = 2;
    public static final byte FM_IX = 3;
    public static final byte FM_DEBUG = 4;
    
    private static final int INTS_IN_SLOT = 12;
    
    public AbstractFreespaceManager(LocalObjectContainer file){
        _file = file;
    }

    public static byte checkType(byte systemType){
        if(systemType == FM_DEFAULT){
            return FM_RAM;    
        }
        return systemType;
    }
    
    public static AbstractFreespaceManager createNew(LocalObjectContainer file){
        return createNew(file, file.systemData().freespaceSystem());
    }
    
	public abstract int onNew(LocalObjectContainer file);
    
    public static AbstractFreespaceManager createNew(LocalObjectContainer file, byte systemType){
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
    
    public void migrateTo(final FreespaceManager fm) {
    	traverse(new Visitor4() {
			public void visit(Object obj) {
				fm.free((Slot) obj);
			}
		});
    }
    
    static void slotEntryToZeroes(LocalObjectContainer file, int address){
        StatefulBuffer writer = new StatefulBuffer(file.systemTransaction(), address, slotLength());
        for (int i = 0; i < INTS_IN_SLOT; i++) {
            writer.writeInt(0);
        }
        if (Debug.xbytes) {
            writer.setID(Const4.IGNORE_ID);  // no XBytes check
        }
        writer.writeEncrypt();
    }
    
    
    final static int slotLength(){
        return Const4.INT_LENGTH * INTS_IN_SLOT;
    }
    
    public final int totalFreespace() {
        final MutableInt mint = new MutableInt();
        traverse(new Visitor4() {
            public void visit(Object obj) {
                Slot slot = (Slot) obj;
                mint.add(slot._length);
            }
        });
        return mint.value();
    }
    
    public abstract void beginCommit();
    
    final int blockSize(){
        return _file.blockSize();
    }
    
    final int discardLimit(){
        return _file.configImpl().discardFreeSpace();
    }
    
    public static void migrate(FreespaceManager oldFM, FreespaceManager newFM) {
    	oldFM.migrateTo(newFM);
    	oldFM.freeSelf();
    	newFM.beginCommit();
    	newFM.endCommit();
    }
    
}
