/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import java.io.IOException;

import com.db4o.*;
import com.db4o.foundation.network.Socket4;
import com.db4o.inside.*;

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

	void fakePayLoad(Transaction a_trans) {
		if (Debug.fakeServer) {
			_payLoad.removeFirstBytes(Const4.INT_LENGTH * 2);
			_payLoad._offset = 0;
			_payLoad.setTransaction(a_trans);
		}
	}

	public Buffer getByteLoad() {
		return _payLoad;
	}

	public final StatefulBuffer payLoad() {
		return _payLoad;
	}
	
	public void payLoad(StatefulBuffer writer) {
		_payLoad = writer;
	}
	
	public final MsgD getWriterForLength(Transaction a_trans, int length) {
		MsgD message = (MsgD)clone(a_trans);
		message._payLoad = new StatefulBuffer(a_trans, length + Const4.MESSAGE_LENGTH);
		message.writeInt(_msgID);
		message.writeInt(length);
		if(a_trans.parentTransaction() == null){
		    message._payLoad.append(Const4.SYSTEM_TRANS);
		}else{
		    message._payLoad.append(Const4.USER_TRANS);
		}
		return message;
	}
	
	public final MsgD getWriter(Transaction a_trans){
		return getWriterForLength(a_trans, 0);
	}
	
	public final MsgD getWriterForInts(Transaction a_trans, int[] ints) {
        MsgD message = getWriterForLength(a_trans, Const4.INT_LENGTH * ints.length);
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
	

	public final MsgD getWriterForString(Transaction a_trans, String str) {
		MsgD message = getWriterForLength(a_trans, Const4.stringIO.length(str) + Const4.INT_LENGTH);
		message.writeString(str);
		return message;
	}

	public MsgD getWriter(StatefulBuffer bytes) {
		MsgD message = getWriterForLength(bytes.getTransaction(), bytes.getLength());
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

	final Msg readPayLoad(Transaction a_trans, Socket4 sock, Buffer reader)
		throws IOException {
		int length = reader.readInt();
		
		a_trans = checkParentTransaction(a_trans, reader);
		
		final MsgD command = (MsgD)clone(a_trans);
		command._payLoad = new StatefulBuffer(a_trans, length);
		command._payLoad.read(sock);
		return command;
	}

	public final String readString() {
		int length = readInt();
		return Const4.stringIO.read(_payLoad, length);
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
