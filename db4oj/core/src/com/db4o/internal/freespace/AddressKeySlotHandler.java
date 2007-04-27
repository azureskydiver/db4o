/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class AddressKeySlotHandler extends SlotHandler{
	
	public int compareTo(Object obj) {
		return _current.compareByAddress((Slot)obj);
	}

}
