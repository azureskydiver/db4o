/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.consistency;

class SlotSource {
	public final static SlotSource ID_SYSTEM = new SlotSource("IdSystem");
	public final static SlotSource FREESPACE = new SlotSource("Freespace");

	private final String _name;
	
	private SlotSource(String name) {
		_name = name;
	}
	
	@Override
	public String toString() {
		return _name;
	}
}