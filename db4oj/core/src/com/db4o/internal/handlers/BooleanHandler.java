/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public final class BooleanHandler extends PrimitiveHandler
{

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final byte TRUE = (byte) 'T';
	private static final byte FALSE = (byte) 'F';
	
	private static final Boolean i_primitive = new Boolean(false);
	
    public BooleanHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
	public int getID(){
		return 4;
	}
	
	public Object defaultValue(){
		return i_primitive;
	}
	
	public int linkLength(){
		return LENGTH;
	}
	
	protected Class primitiveJavaClass(){
		return boolean.class;
	}
	
	public Object primitiveNull(){
		return i_primitive;
	}

	Object read1(Buffer a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPBOOLEAN);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		
		if(ret == TRUE){
			return new Boolean(true);
		}
		if(ret == FALSE){
			return new Boolean(false);
		}
		
		return null;
	}
	
	public void write(Object a_object, Buffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPBOOLEAN);
		}
		byte set;
		if(((Boolean)a_object).booleanValue()){
			set = TRUE;
		}else{
			set = FALSE;
		}
		a_bytes.append(set);
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}

	
	// Comparison_______________________
	
	private boolean i_compareTo;
	
	private boolean val(Object obj){
		return ((Boolean)obj).booleanValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
    
    public Object current1(){
        return new Boolean(i_compareTo);
    }
	
	boolean isEqual1(Object obj){
		return obj instanceof Boolean && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
	    if(i_compareTo){
	        return false;
	    }
		return obj instanceof Boolean && val(obj);
	}
	
	boolean isSmaller1(Object obj){
	    if(! i_compareTo){
	        return false;
	    }
	    return obj instanceof Boolean && ! val(obj);
	}
}
