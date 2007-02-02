/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;



public final class CharHandler extends PrimitiveHandler {

    static final int LENGTH = Const4.CHAR_BYTES + Const4.ADDED_LENGTH;
	
	private static final Character i_primitive = new Character((char)0);
	
    public CharHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
	public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID() {
		return 7;
	}

	public int linkLength() {
		return LENGTH;
	}

	protected Class primitiveJavaClass() {
		return char.class;
	}

	public Object primitiveNull() {
		return i_primitive;
	}

	Object read1(Buffer a_bytes) {
		if (Deploy.debug) {
			a_bytes.readBegin(Const4.YAPCHAR);
		}
		byte b1 = a_bytes.readByte();
		byte b2 = a_bytes.readByte();
		if (Deploy.debug) {
			a_bytes.readEnd();
		}
		char ret = (char) ((b1 & 0xff) | ((b2 & 0xff) << 8));
		return new Character(ret);
	}

	public void write(Object a_object, Buffer a_bytes) {
		if (Deploy.debug) {
			a_bytes.writeBegin(Const4.YAPCHAR);
		}
		char char_ = ((Character) a_object).charValue();
		a_bytes.append((byte) (char_ & 0xff));
		a_bytes.append((byte) (char_ >> 8));
		if (Deploy.debug) {
			a_bytes.writeEnd();
		}
	}

	// Comparison_______________________

	private char i_compareTo;

	private char val(Object obj) {
		return ((Character) obj).charValue();
	}

	void prepareComparison1(Object obj) {
		i_compareTo = val(obj);
	}
    
    public Object current1(){
        return new Character(i_compareTo);
    }

	boolean isEqual1(Object obj) {
		return obj instanceof Character && val(obj) == i_compareTo;
	}

	boolean isGreater1(Object obj) {
		return obj instanceof Character && val(obj) > i_compareTo;
	}

	boolean isSmaller1(Object obj) {
		return obj instanceof Character && val(obj) < i_compareTo;
	}

}
