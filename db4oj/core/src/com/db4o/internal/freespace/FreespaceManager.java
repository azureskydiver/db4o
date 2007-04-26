/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import java.io.*;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface FreespaceManager {
	
	
	// TODO: FB delete method when FreespaceManagerIx is removed
	public void onNew(LocalObjectContainer file);

	// TODO: FB delete method when FreespaceManagerIx is removed
	public void beginCommit();

	// TODO: FB delete method when FreespaceManagerIx is removed
	public void endCommit();
	
	public int slotCount();

	public void free(Slot slot);
	
    public void freeSelf();

	public int totalFreespace();

	public Slot getSlot(int length);

	public void migrateTo(FreespaceManager fm);

	public void read(int freeSpaceID);

//	 TODO: FB delete method when FreespaceManagerIx is removed
	public void start(int slotAddress) throws IOException;

	public byte systemType();

	public int write();

}