/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.slots;

import com.db4o.*;

public final class ReferencedSlot extends Slot{
    
	public int _references;
	
	public ReferencedSlot(int address, int length){
        super(address, length);
	}
	
	public Object read(YapReader a_bytes){
		int address = a_bytes.readInt();
		int length = a_bytes.readInt();
		return new ReferencedSlot(address, length);
	}
}
