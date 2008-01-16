/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public abstract class AbstractFreespaceManager implements FreespaceManager {
    
    final LocalObjectContainer     _file;

    public static final byte FM_DEBUG = 127;
    public static final byte FM_DEFAULT = 0;
    public static final byte FM_LEGACY_RAM = 1;
    public static final byte FM_RAM = 2;
    public static final byte FM_IX = 3;
    public static final byte FM_BTREE = 4;
    
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
    
    public static AbstractFreespaceManager createNew(LocalObjectContainer file, byte systemType){
        systemType = checkType(systemType);
        switch(systemType){
        	case FM_IX:
        		return new FreespaceManagerIx(file);
        	case FM_BTREE:
        		return new BTreeFreespaceManager(file);
            default:
                return new RamFreespaceManager(file);
        }
    }
    
    public static int initSlot(LocalObjectContainer file){
        int address = file.getSlot(slotLength()).address();
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
    
    public int totalFreespace() {
        final IntByRef mint = new IntByRef();
        traverse(new Visitor4() {
            public void visit(Object obj) {
                Slot slot = (Slot) obj;
                mint.value += slot.length();
            }
        });
        return mint.value;
    }
    
    public abstract void beginCommit();
    
    protected final int blockedDiscardLimit(){
        return _file.blocksToBytes(discardLimit());
    }

	protected int discardLimit() {
		return _file.configImpl().discardFreeSpace();
	}
    
    final boolean canDiscard(int blocks) {
		return blocks == 0 || blocks < blockedDiscardLimit();
	}
    
    public static void migrate(FreespaceManager oldFM, FreespaceManager newFM) {
    	oldFM.migrateTo(newFM);
    	oldFM.freeSelf();
    }
    
    public void debugCheckIntegrity(){
        final IntByRef lastStart = new IntByRef();
        final IntByRef lastEnd = new IntByRef();
        traverse(new Visitor4() {
            public void visit(Object obj) {
                Slot slot = (Slot) obj;
                if(slot.address() <= lastEnd.value){
                    throw new IllegalStateException();
                }
                lastStart.value = slot.address();
                lastEnd.value = slot.address() + slot.length();
            }
        });
        
    }
    
    protected final LocalTransaction transaction(){
        return (LocalTransaction)_file.systemTransaction();
    }

	public static boolean migrationRequired(byte systemType) {
		return systemType == FM_LEGACY_RAM  || systemType == FM_IX ;
	}

    
}
