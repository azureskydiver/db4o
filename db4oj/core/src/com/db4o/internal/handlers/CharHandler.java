/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;



public final class CharHandler extends PrimitiveHandler {

    static final int LENGTH = Const4.CHAR_BYTES + Const4.ADDED_LENGTH;
	
	private static final Character i_primitive = new Character((char)0);
	
    public CharHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
	public Object defaultValue(){
		return i_primitive;
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

	Object read1(BufferImpl a_bytes) {
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

	public void write(Object a_object, BufferImpl a_bytes) {
		if (Deploy.debug) {
			a_bytes.writeBegin(Const4.YAPCHAR);
		}
		char char_ = ((Character) a_object).charValue();
		a_bytes.writeByte((byte) (char_ & 0xff));
		a_bytes.writeByte((byte) (char_ >> 8));
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

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPCHAR);
        }
        
        byte b1 = context.readByte();
        byte b2 = context.readByte();
        char charValue = (char) ((b1 & 0xff) | ((b2 & 0xff) << 8));
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return new Character(charValue);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPCHAR);
        }
        
        char charValue = ((Character) obj).charValue();
        
        context.writeBytes(new byte[]{
            (byte)(charValue & 0xff),
            (byte)(charValue >> 8)
        });
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final char sourceChar = ((Character)source).charValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				char targetChar = ((Character)target).charValue();
				return sourceChar == targetChar ? 0 : (sourceChar < targetChar ? - 1 : 1); 
			}
		};
    }


}
