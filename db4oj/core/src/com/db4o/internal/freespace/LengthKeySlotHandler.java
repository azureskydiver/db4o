/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class LengthKeySlotHandler extends SlotHandler{
	
	public int compareTo(Object obj) {
		return _current.compareByLength((Slot)obj);
	}
	
	public PreparedComparison newPrepareCompare(Object obj) {
		final Slot sourceSlot = (Slot)obj;
		return new PreparedComparison() {
			public int compareTo(Object obj) {
				final Slot targetSlot = (Slot)obj;
				
				// FIXME: The comparison method in #compareByLength is the wrong way around.
				
				// Fix there and here after other references are fixed.
				
				return - sourceSlot.compareByLength(targetSlot);
			}
		};
	}



}
