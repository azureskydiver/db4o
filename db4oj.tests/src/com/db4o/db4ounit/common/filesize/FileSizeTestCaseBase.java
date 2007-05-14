/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.filesize;

import java.io.*;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.extensions.*;

public abstract class FileSizeTestCaseBase extends AbstractDb4oTestCase {

    public static class Item{
    	public int _int; 
    }

     protected int fileSize() {
    	LocalObjectContainer localContainer = fixture().fileSession();
        IoAdaptedObjectContainer container = (IoAdaptedObjectContainer) localContainer;
        container.syncFiles();
        long length = new File(container.fileName()).length();
        return (int)length;
    }

    protected void produceSomeFreeSpace() {
        FreespaceManager fm = container().freespaceManager();
        int length = 300;
        Slot slot = container().getSlot(length);
        Buffer buffer = new Buffer(length);
        container().writeBytes(buffer, slot.address(), 0);
        fm.free(slot);
    }

    protected void storeSomeItems() {
    	for (int i = 0; i < 3; i++) {
    		store(new Item());
    	}
    	db().commit();
    }
    
    protected LocalObjectContainer container() {
        return fixture().fileSession();
    }

}