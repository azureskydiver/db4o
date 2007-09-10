/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;


public class MsgObject extends MsgD {
	
	private static final int LENGTH_FOR_ALL = Const4.ID_LENGTH + (Const4.INT_LENGTH * 2);
	private static final int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;
	
	private int _id;
	private int _address;
	
	MsgD getWriter(StatefulBuffer bytes, int[] prependInts) {
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
		bytes.appendTo(message._payLoad, -1);
		return message;
	}

	public MsgD getWriter(StatefulBuffer bytes) {
		return getWriter(bytes, null);
	}
	
	public MsgD getWriter(ClassMetadata a_yapClass, StatefulBuffer bytes) {
        if(a_yapClass == null){
            return getWriter(bytes, new int[]{0});
        }
		return getWriter(bytes, new int[]{ a_yapClass.getID()});
	}
	
	public MsgD getWriter(ClassMetadata a_yapClass, int a_param, StatefulBuffer bytes) {
		return getWriter(bytes, new int[]{ a_yapClass.getID(), a_param});
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
