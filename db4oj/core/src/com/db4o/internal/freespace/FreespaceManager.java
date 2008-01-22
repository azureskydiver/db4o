/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface FreespaceManager {
	
	public void beginCommit();

	public void endCommit();
	
	public int slotCount();

	public void free(Slot slot);
	
    public void freeSelf();

	public int totalFreespace();

	public Slot getSlot(int length);

	public void migrateTo(FreespaceManager fm);

	public void read(int freeSpaceID);

	public void start(int slotAddress);

	public byte systemType();
	
	public void traverse(Visitor4 visitor);

	public int write();

	public void commit();

	public Slot allocateTransactionLogSlot(int length);

	public void freeTransactionLogSlot(Slot slot);

}