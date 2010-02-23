/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class IdSlotMapping extends TreeInt {
	
	private final Slot _slot;

	public IdSlotMapping(int id, Slot slot) {
		super(id);
		_slot = slot;
	}

}
