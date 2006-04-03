/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


class MsgObject extends MsgD {
	private static final int LENGTH_FOR_ALL = YapConst.YAPID_LENGTH + (YapConst.YAPINT_LENGTH * 3);
	private static final int LENGTH_FOR_FIRST = LENGTH_FOR_ALL;
	
	int _id;
	int _address;
	
	public MsgObject() {
		super();
	}

	public MsgObject(MsgCloneMarker marker) {
		super(marker);
	}

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
		        message._payLoad.writeInt(prependInts[i]);    
            }
		}
		message._payLoad.writeInt(embeddedCount);
		bytes.appendTo(message._payLoad, -1);
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
		_payLoad.setTransaction(getTransaction());
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
			YapWriter[] embedded = new YapWriter[embeddedCount + 1];
			embedded[0] = _payLoad;
			new YapWriter(_payLoad, embedded, 1);  // this line cascades and adds all embedded YapBytes 
			_payLoad.trim4(LENGTH_FOR_FIRST + addLengthBeforeFirst, length);
		}
		_payLoad.useSlot(_id, _address, length);
		return _payLoad;
	}

	protected Msg shallowCloneInternal(Msg msg) {
		MsgObject clone=(MsgObject)super.shallowCloneInternal(msg);
		clone._id=_id;
		clone._address=_address;
		return clone;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MsgObject(MsgCloneMarker.INSTANCE));
	}
}
