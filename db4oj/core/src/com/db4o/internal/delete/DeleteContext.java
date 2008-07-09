/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.delete;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.Slot;
import com.db4o.marshall.Context;
import com.db4o.marshall.ReadBuffer;


/**
 * @exclude
 */
public interface DeleteContext extends Context, ReadBuffer, HandlerVersionContext{
    
    public boolean cascadeDelete();

	public int cascadeDeleteDepth();
	
	public void delete(TypeHandler4 handler);
	
	public void deleteObject();

	boolean isLegacyHandlerVersion();

	int handlerVersion();
	
	void defragmentRecommended();

	Slot readSlot();
	
}
