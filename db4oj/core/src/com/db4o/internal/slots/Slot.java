/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class Slot {
    
    public final int _address;
    
    public final int _length;

    public Slot(int address, int length){
        _address = address;
        _length = length;
    }
    
    public int getAddress() {
        return _address;
    }

    public int getLength() {
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
        return (_address == other._address) && (_length == other._length);
    }
    
    public int hashCode() {
        return _address ^ _length;
    }
    
    public String toString() {
    	return "[A:"+_address+",L:"+_length+"]";
    }
    
    public static int MARSHALLED_LENGTH = Const4.INT_LENGTH * 2;

	public int compareByAddress(Slot slot) {
		return slot._address - _address;
	}
	
	public int compareByLength(Slot slot) {
		int res = slot._length - _length;
		if(res != 0){
			return res;
		}
		return compareByAddress(slot);
	}
    
}
