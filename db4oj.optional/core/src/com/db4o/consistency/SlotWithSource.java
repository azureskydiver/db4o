/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.consistency;

import com.db4o.internal.slots.*;

class SlotWithSource {
	public final Slot _slot;
	public final SlotSource _source;

	public SlotWithSource(Slot slot, SlotSource source) {
		this._slot = slot;
		this._source = source;
	}
	
	@Override
	public String toString() {
		return _slot + "(" + _source + ")";
	}
}