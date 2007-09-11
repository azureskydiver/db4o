/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;


public class MsgObject extends MsgD {
	
	private static final int LENGTH_FOR_ALL = Const4.ID_LENGTH + (Const4.INT_LENGTH * 2);
	private static final int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;
	
	private int _id;
	private int _address;
	
	final MsgD getWriter(Pointer4 pointer, StatefulBuffer bytes,int[] prependInts) {
		int lengthNeeded = bytes.length() + LENGTH_FOR_FIRST;
		if(prependInts != null){
			lengthNeeded += (prependInts.length * Const4.INT_LENGTH);
		}
		MsgD message = getWriterForLength(bytes.getTransaction(), lengthNeeded);
		if(prependInts != null){
		    for (int i = 0; i < prependInts.length; i++) {
		        message._payLoad.writeInt(prependInts[i]);    
            }
		}
		message._payLoad.append(pointer, bytes);
		return message;
	}

	final public MsgD getWriter(StatefulBuffer buffer) {
		return getWriter(buffer.pointer(), buffer, null);
	}
	
	public final MsgD getWriter(Pointer4 pointer, ClassMetadata a_yapClass, StatefulBuffer bytes) {
        if(a_yapClass == null){
            return getWriter(pointer, bytes, new int[]{0});
        }
		return getWriter(pointer, bytes, new int[]{ a_yapClass.getID()});
	}
	
	public final MsgD getWriter(Pointer4 pointer, ClassMetadata classMetadata, int param, StatefulBuffer buffer) {
		return getWriter(pointer, buffer, new int[]{ classMetadata.getID(), param});
	}
	
	public final StatefulBuffer unmarshall() {
		return unmarshall(0);
	}

	public final StatefulBuffer unmarshall(int addLengthBeforeFirst) {
		_payLoad.setTransaction(transaction());
		
		int length = _payLoad.readInt();
		if (length == 0) {
			return null;  // does this happen ?
		}
		_id = _payLoad.readInt();
		_address = _payLoad.readInt();
		_payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
		_payLoad.useSlot(_id, _address, length);
		return _payLoad;
	}

	public int getId() {
		return _id;
	}
}
