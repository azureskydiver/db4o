/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

class MsgObject extends MsgD {
	private static final int LENGTH_FOR_ALL = YapConst.YAPID_LENGTH + (YapConst.YAPINT_LENGTH * 3);
	private static final int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;
	
	int i_id;
	int i_address;
	
	MsgD getWriter(YapWriter bytes, int[] prependInts) {
		int lengthNeeded = bytes.getLength() + LENGTH_FOR_FIRST;
		if(prependInts != null){
			lengthNeeded += (prependInts.length * YapConst.YAPINT_LENGTH);
		}
		int embeddedCount = bytes.embeddedCount();
		if(embeddedCount > 0){
			lengthNeeded += (LENGTH_FOR_ALL * embeddedCount) + bytes.embeddedLength();
		}
		MsgD message = getWriterForLength(bytes.getTransaction(), lengthNeeded);
		if(prependInts != null){
		    for (int i = 0; i < prependInts.length; i++) {
		        message.payLoad.writeInt(prependInts[i]);    
            }
		}
		message.payLoad.writeInt(embeddedCount);
		bytes.appendTo(message.payLoad, -1);
		return message;
	}

	MsgD getWriter(YapWriter bytes) {
		return getWriter(bytes, null);
	}
	
	MsgD getWriter(YapClass a_yapClass, YapWriter bytes) {
		return getWriter(bytes, new int[]{ a_yapClass.getID()});
	}
	
	MsgD getWriter(YapClass a_yapClass, int a_param, YapWriter bytes) {
		return getWriter(bytes, new int[]{ a_yapClass.getID(), a_param});
	}
	
	public final YapWriter unmarshall() {
		return unmarshall(0);
	}

	public final YapWriter unmarshall(int addLengthBeforeFirst) {
		payLoad.setTransaction(getTransaction());
		int embeddedCount = payLoad.readInt();
		int length = payLoad.readInt();
		if (length == 0) {
			return null;  // does this happen ?
		}
		i_id = payLoad.readInt();
		i_address = payLoad.readInt();
		if(embeddedCount == 0){
			payLoad.removeFirstBytes(LENGTH_FOR_FIRST + addLengthBeforeFirst);
		}else{
			payLoad.i_offset += length;
			YapWriter[] embedded = new YapWriter[embeddedCount + 1];
			embedded[0] = payLoad;
			new YapWriter(payLoad, embedded, 1);  // this line cascades and adds all embedded YapBytes 
			payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
		}
		payLoad.useSlot(i_id, i_address, length);
		return payLoad;
	}

}
