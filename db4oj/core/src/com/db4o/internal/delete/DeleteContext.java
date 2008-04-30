/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.delete;

import com.db4o.internal.slots.Slot;
import com.db4o.marshall.Context;
import com.db4o.marshall.ReadBuffer;


/**
 * @exclude
 */
public interface DeleteContext extends Context, ReadBuffer{
    
    public boolean cascadeDelete();

	public int cascadeDeleteDepth();

	boolean isLegacyHandlerVersion();

	int handlerVersion();
	
	void defragmentRecommended();

	Slot readSlot();
	
}
