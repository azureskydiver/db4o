/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;


/**
 * @exclude
 */
public final class YapBit{
	
	private int i_value;
	
	public YapBit(int a_value){
		i_value = a_value;
	}
	
	public void set(boolean a_bit){
		i_value <<= 1;
		if(a_bit){
			i_value|=1;
		}
	}
	
	public boolean get(){
		boolean ret=((i_value&1)!=0);
		i_value >>= 1;
		return ret;
	}
	
	public byte getByte(){
		return (byte)i_value;
	}
}
