/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


final class YShort extends YapJavaClass
{
	static final int LENGTH = YapConst.SHORT_BYTES + YapConst.ADDED_LENGTH;
	
	private static final Short i_primitive = new Short((short)0);
	private static final Class i_class = i_primitive.getClass();
	
	public int getID(){
		return 8;
	}
	
	public Class getJavaClass(){
		return i_class;
	}
	
	public Class getPrimitiveJavaClass(){
		return short.class;
	}
	
	public int linkLength(){
		return LENGTH;
	}
	
	Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(YapReader a_bytes){
		short ret = readShort(a_bytes);
		if(! Deploy.csharp){
			if(ret == Short.MAX_VALUE){
				return null;
			}
		}
		return new Short(ret);
	}
	
	static final short readShort(YapReader a_bytes){
		int ret = 0;
		if (Deploy.debug){
			a_bytes.readBegin(YapConst.YAPSHORT);
		}
		for (int i = 0; i < YapConst.SHORT_BYTES; i++){
			ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
		}
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return (short)ret;
	}

	public void write(Object a_object, YapWriter a_bytes){
		if (! Deploy.csharp && a_object == null){
			writeShort(Short.MAX_VALUE,a_bytes);
		} else {
			writeShort(((Short)a_object).shortValue(), a_bytes);
		}
	}
	
	static final void writeShort(int a_short, YapWriter a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(YapConst.YAPSHORT);
		}
		for (int i = 0; i < YapConst.SHORT_BYTES; i++){
			a_bytes._buffer[a_bytes._offset++] = (byte) (a_short >> ((YapConst.SHORT_BYTES - 1 - i) * 8));
		}
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
	
	// Comparison_______________________
	
	private short i_compareTo;
	
	private short val(Object obj){
		return ((Short)obj).shortValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
	
	boolean isEqual1(Object obj){
		return obj instanceof Short && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Short && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Short && val(obj) < i_compareTo;
	}
	
	
}
