/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.slots;

import com.db4o.*;


public class Slot implements ReadWriteable {
    
    public int _address;
    public int _length;
    
    public Slot(int address, int length){
        _address = address;
        _length = length;
    }

    public int byteCount(){
        return YapConst.YAPINT_LENGTH * 2;
    }
    
    public void write(YapWriter a_bytes){
        a_bytes.writeInt(_address);
        a_bytes.writeInt(_length);
    }
    
    public Object read(YapReader a_bytes){
        int address = a_bytes.readInt();
        int length = a_bytes.readInt();
        return new Slot(address, length);
    }

}
