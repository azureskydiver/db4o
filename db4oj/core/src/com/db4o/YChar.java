/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


final class YChar extends YapJavaClass {
	static final int LENGTH = YapConst.CHAR_BYTES + YapConst.ADDED_LENGTH;
	
	private static final Character i_primitive = new Character((char)0);
	private static final Class i_class = i_primitive.getClass();
	

	public int getID() {
		return 7;
	}

	public Class getJavaClass() {
		return i_class;
	}

	public Class getPrimitiveJavaClass() {
		return char.class;
	}

	public int linkLength() {
		return LENGTH;
	}

	Object primitiveNull() {
		return i_primitive;
	}

	Object read1(YapReader a_bytes) {
		if (Deploy.debug) {
			a_bytes.readBegin(YapConst.YAPCHAR);
		}
		byte b1 = a_bytes.readByte();
		byte b2 = a_bytes.readByte();
		if (Deploy.debug) {
			a_bytes.readEnd();
		}
		char ret = (char) ((b1 & 0xff) | ((b2 & 0xff) << 8));
		if(! Deploy.csharp){
			if (ret == Character.MAX_VALUE) {
				return null;
			}
		}
		return new Character(ret);
	}

	public void write(Object a_object, YapWriter a_bytes) {
		if (Deploy.debug) {
			a_bytes.writeBegin(YapConst.YAPCHAR);
		}
		char l_char;
		if (! Deploy.csharp && a_object == null) {
			l_char = Character.MAX_VALUE;
		} else {
			l_char = ((Character) a_object).charValue();
		}
		a_bytes.append((byte) (l_char & 0xff));
		a_bytes.append((byte) (l_char >> 8));
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
