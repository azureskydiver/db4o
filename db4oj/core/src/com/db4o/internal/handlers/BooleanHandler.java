/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;


/**
 * @exclude
 */
public final class BooleanHandler extends PrimitiveHandler {

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final byte TRUE = (byte) 'T';
	private static final byte FALSE = (byte) 'F';
	private static final byte NULL = (byte) 'N';
	
	private static final Boolean i_primitive = new Boolean(false);
	
    public BooleanHandler(ObjectContainerBase stream) {
        super(stream);
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

	Object read1(BufferImpl a_bytes){
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
	
	public void write(Object obj, BufferImpl buffer){
		if(Deploy.debug){
			buffer.writeBegin(Const4.YAPBOOLEAN);
		}		
		buffer.writeByte(getEncodedByteValue(obj));
		if(Deploy.debug){
			buffer.writeEnd();
		}
	}

	
	private byte getEncodedByteValue(Object obj) {
		if (obj == null) {
			return NULL;
		}
		if (((Boolean)obj).booleanValue()) {
			return TRUE;
		}
		return FALSE;
	}


	// Comparison_______________________
	
	private boolean i_compareTo;
	
	private boolean val(Object obj){
		return ((Boolean)obj).booleanValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
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
	
	public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPBOOLEAN);
        }
        
		byte ret = context.readByte();
		
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
		if(ret == TRUE){
			return new Boolean(true);
		}
		if(ret == FALSE){
			return new Boolean(false);
		}
		return null;
	}
	
	public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPBOOLEAN);
        }
		context.writeByte(getEncodedByteValue(obj));
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
	}
	
    public Object nullRepresentationInUntypedArrays(){
        return null;
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final boolean sourceBoolean = ((Boolean)source).booleanValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				boolean targetBoolean = ((Boolean)target).booleanValue();
				return sourceBoolean == targetBoolean ? 0 : (sourceBoolean ? 1 : -1); 
			}
		};
    }

	
}
