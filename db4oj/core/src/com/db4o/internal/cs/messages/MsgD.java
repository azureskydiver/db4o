/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.network.*;
import com.db4o.internal.*;

/**
 * Messages with Data for Client/Server Communication
 */
public class MsgD extends Msg{

	StatefulBuffer _payLoad;

	MsgD() {
		super();
	}

	MsgD(String aName) {
		super(aName);
	}

	public ByteArrayBuffer getByteLoad() {
		return _payLoad;
	}

	public final StatefulBuffer payLoad() {
		return _payLoad;
	}
	
	public void payLoad(StatefulBuffer writer) {
		_payLoad = writer;
	}
	
	public final MsgD getWriterForByte(Transaction trans, byte b) {
		MsgD msg = getWriterForLength(trans, 1);
		msg._payLoad.writeByte(b);
		return msg;
	}
	
	public final MsgD getWriterForLength(Transaction trans, int length) {
		MsgD message = (MsgD)publicClone();
		message.setTransaction(trans);
		message._payLoad = new StatefulBuffer(trans, length + Const4.MESSAGE_LENGTH);
		message.writeInt(_msgID);
		message.writeInt(length);
		if(trans.parentTransaction() == null){
		    message._payLoad.writeByte(Const4.SYSTEM_TRANS);
		}else{
		    message._payLoad.writeByte(Const4.USER_TRANS);
		}
		return message;
	}
	
	public final MsgD getWriter(Transaction trans){
		return getWriterForLength(trans, 0);
	}
	
	public final MsgD getWriterForInts(Transaction trans, int[] ints) {
        MsgD message = getWriterForLength(trans, Const4.INT_LENGTH * ints.length);
        for (int i = 0; i < ints.length; i++) {
            message.writeInt(ints[i]);
        }
        return message;
    }
	
    public final MsgD getWriterForIntArray(Transaction a_trans, int[] ints, int length){
		MsgD message = getWriterForLength(a_trans, Const4.INT_LENGTH * (length + 1));
		message.writeInt(length);
		for (int i = 0; i < length; i++) {
			message.writeInt(ints[i]);
		}
		return message;
	}

	public final MsgD getWriterForInt(Transaction a_trans, int id) {
		MsgD message = getWriterForLength(a_trans, Const4.INT_LENGTH);
		message.writeInt(id);
		return message;
	}
	
	public final MsgD getWriterForIntString(Transaction a_trans,int anInt, String str) {
		MsgD message = getWriterForLength(a_trans, Const4.stringIO.length(str) + Const4.INT_LENGTH * 2);
		message.writeInt(anInt);
		message.writeString(str);
		return message;
	}
	
	public final MsgD getWriterForLong(Transaction a_trans, long a_long){
		MsgD message = getWriterForLength(a_trans, Const4.LONG_LENGTH);
		message.writeLong(a_long);
		return message;
	}
	
	public MsgD getWriterForSingleObject(Transaction trans, Object obj) {
		SerializedGraph serialized = Serializer.marshall(trans.container(), obj);
		MsgD msg = getWriterForLength(trans, serialized.marshalledLength());
		serialized.write(msg._payLoad);
		return msg;
	}

	public final MsgD getWriterForString(Transaction a_trans, String str) {
		MsgD message = getWriterForLength(a_trans, Const4.stringIO.length(str) + Const4.INT_LENGTH);
		message.writeString(str);
		return message;
	}

	public MsgD getWriter(StatefulBuffer bytes) {
		MsgD message = getWriterForLength(bytes.getTransaction(), bytes.length());
		message._payLoad.append(bytes._buffer);
		return message;
	}
	
	public byte[] readBytes(){
	    return _payLoad.readBytes(readInt());
	}

	public final int readInt() {
		return _payLoad.readInt();
	}
	
	public final long readLong(){
	    return _payLoad.readLong();
	}
	
	public final boolean readBoolean() {
		return _payLoad.readByte() != 0;
	}
	
	public Object readObjectFromPayLoad(){
		return Serializer.unmarshall(stream(),_payLoad);
	}

	final Msg readPayLoad(MessageDispatcher messageDispatcher, Transaction a_trans, Socket4 sock, ByteArrayBuffer reader){
		int length = reader.readInt();
		a_trans = checkParentTransaction(a_trans, reader);
		final MsgD command = (MsgD)publicClone();
		command.setTransaction(a_trans);
		command.setMessageDispatcher(messageDispatcher);
		command._payLoad = readMessageBuffer(a_trans, sock, length);
        return command;
	}

	public final String readString() {
		int length = readInt();
		return Const4.stringIO.read(_payLoad, length);
	}
	
	public Object readSingleObject() {
		return Serializer.unmarshall(stream(), SerializedGraph.read(_payLoad));
	}
	
	public final void writeBytes(byte[] aBytes){
	    writeInt(aBytes.length);
	    _payLoad.append(aBytes);
	}

	public final void writeInt(int aInt) {
		_payLoad.writeInt(aInt);
	}
	
	public final void writeLong(long l){
        _payLoad.writeLong(l);
	}

	public final void writeString(String aStr) {
		_payLoad.writeInt(aStr.length());
		Const4.stringIO.write(_payLoad, aStr);
	}

}
