/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class AddressKeySlotHandler extends SlotHandler{
	
	public int compareTo(Object obj) {
		return _current.compareByAddress((Slot)obj);
	}
	
	public PreparedComparison prepareComparison(Object obj) {
		final Slot sourceSlot = (Slot)obj;
		return new PreparedComparison() {
			public int compareTo(Object obj) {
				final Slot targetSlot = (Slot)obj;
				
				// FIXME: The comparison method in #compareByAddress is the wrong way around.
				
				// Fix there and here after other references are fixed.
				
				return - sourceSlot.compareByAddress(targetSlot);
			}
		};
	}

}
