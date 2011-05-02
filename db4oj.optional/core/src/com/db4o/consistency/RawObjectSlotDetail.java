package com.db4o.consistency;

import com.db4o.internal.slots.*;

public class RawObjectSlotDetail extends SlotDetail {

	public RawObjectSlotDetail(Slot slot) {
		super(slot);
	}

	@Override
	public String toString() {
		return "OBJ: " + _slot;
	}
}
