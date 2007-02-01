/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.inside.*;


public class MsgObject extends MsgD {
	
	private static final int LENGTH_FOR_ALL = YapConst.ID_LENGTH + (YapConst.INT_LENGTH * 3);
	private static final int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;
	
	private int _id;
	private int _address;
	
	MsgD getWriter(StatefulBuffer bytes, int[] prependInts) {
		int lengthNeeded = bytes.getLength() + LENGTH_FOR_FIRST;
		if(prependInts != null){
			lengthNeeded += (prependInts.length * YapConst.INT_LENGTH);
		}
		int embeddedCount = bytes.embeddedCount();
		if(embeddedCount > 0){
			lengthNeeded += (LENGTH_FOR_ALL * embeddedCount) + bytes.embeddedLength();
		}
		MsgD message = getWriterForLength(bytes.getTransaction(), lengthNeeded);
		if(prependInts != null){
		    for (int i = 0; i < prependInts.length; i++) {
		        message._payLoad.writeInt(prependInts[i]);    
            }
		}
		message._payLoad.writeInt(embeddedCount);
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
		int embeddedCount = _payLoad.readInt();
		int length = _payLoad.readInt();
		if (length == 0) {
			return null;  // does this happen ?
		}
		_id = _payLoad.readInt();
		_address = _payLoad.readInt();
		if(embeddedCount == 0){
			_payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
		}else{
			_payLoad._offset += length;
			StatefulBuffer[] embedded = new StatefulBuffer[embeddedCount + 1];
			embedded[0] = _payLoad;
			new StatefulBuffer(_payLoad, embedded, 1);  // this line cascades and adds all embedded YapBytes 
			_payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
		}
		_payLoad.useSlot(_id, _address, length);
		return _payLoad;
	}

	public int getId() {
		return _id;
	}
}
