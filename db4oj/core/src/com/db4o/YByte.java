/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


final class YByte extends YapJavaClass
{

    static final int LENGTH = 1 + YapConst.ADDED_LENGTH;
	
	private static final Byte i_primitive = new Byte((byte)0);
	private static final Class i_class = i_primitive.getClass();
	
    public YByte(YapStream stream) {
        super(stream);
    }
    
	public int getID(){
		return 6;
	}
	
	public Class getJavaClass(){
		return i_class;
	}
	
	public Class getPrimitiveJavaClass(){
		return byte.class;
	}
	
	boolean isNoConstraint(Object obj, boolean isPrimitive){
		return obj.equals(primitiveNull());
	}
	
	public int linkLength(){
		return LENGTH;
	}

	Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(YapReader a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(YapConst.YAPBYTE);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return new Byte(ret);
	}
	
	public void write(Object a_object, YapWriter a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(YapConst.YAPBYTE);
		}
		byte set;
		if (a_object == null){
			set = (byte)0;
		} else {
			set = ((Byte)a_object).byteValue();
		}
		a_bytes.append(set);
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
	
					
	// Comparison_______________________
	
	private byte i_compareTo;
	
	private byte val(Object obj){
		return ((Byte)obj).byteValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
	
	boolean isEqual1(Object obj){
		return obj instanceof Byte && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Byte && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Byte && val(obj) < i_compareTo;
	}
}
