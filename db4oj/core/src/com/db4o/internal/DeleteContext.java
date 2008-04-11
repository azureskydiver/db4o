/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.slots.Slot;
import com.db4o.marshall.Context;
import com.db4o.marshall.ReadBuffer;


/**
 * @exclude
 */
public interface DeleteContext extends Context, ReadBuffer{

	public void cascadeDeleteDepth(int depth);
	
	public int cascadeDeleteDepth();

	boolean isLegacyHandlerVersion();

	int handlerVersion();
	
	void defragmentRecommended();

	Slot readSlot();
	
}
