/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import java.io.*;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface FreespaceManager {
	
	
	// TODO: FB delete method
	public abstract void onNew(LocalObjectContainer file);

	public abstract void beginCommit();

	public abstract void debug();

	public abstract void endCommit();

	public abstract int entryCount();

	public abstract void free(Slot slot);

	public abstract int totalFreespace();

	public abstract int getSlot(int length);

	public abstract void migrate(FreespaceManager newFM);

	public abstract void read(int freeSlotsID);

	public abstract void start(int slotAddress) throws IOException;

	public abstract byte systemType();

	public abstract int shutdown();

	public abstract boolean requiresMigration(byte configuredSystem, byte readSystem);

}