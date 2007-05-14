/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class Slot {
    
    private final int _address;
    
    private final int _length;
    
    public static final Slot ZERO = new Slot(0, 0);

    public Slot(int address, int length){
        _address = address;
        _length = length;
    }
    
    public int address() {
        return _address;
    }

	public int length() {
		return _length;
	}

    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(! (obj instanceof Slot)){
            return false;
        }
        Slot other = (Slot) obj;
        return (_address == other._address) && (length() == other.length());
    }
    
    public int hashCode() {
        return _address ^ length();
    }
    
	public Slot subSlot(int offset) {
		return new Slot(_address + offset, length() - offset);
	}

    public String toString() {
    	return "[A:"+_address+",L:"+length()+"]";
    }
    
	public Slot truncate(int requiredLength) {
		return new Slot(_address, requiredLength);
	}
    
    public static int MARSHALLED_LENGTH = Const4.INT_LENGTH * 2;

	public int compareByAddress(Slot slot) {
        int res = slot._address - _address;
        if(res != 0){
            return res;
        }
        return slot.length() - length();
	}
	
	public int compareByLength(Slot slot) {
		int res = slot.length() - length();
		if(res != 0){
			return res;
		}
		return slot._address - _address;
	}

	public boolean isDirectlyPreceding(Slot other) {
		return _address + length() == other._address;
	}

	public Slot append(Slot slot) {
		return new Slot(address(), _length + slot.length());
	}
	
}
