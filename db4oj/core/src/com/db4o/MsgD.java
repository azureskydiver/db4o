/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

/**
 * Messages with Data for Client/Server Communication
 */
class MsgD extends Msg{

	YapWriter payLoad;

	MsgD() {
		super();
	}

	MsgD(String aName) {
		super(aName);
	}

	void fakePayLoad(Transaction a_trans) {
		if (Debug.fakeServer) {
			payLoad.removeFirstBytes(YapConst.YAPINT_LENGTH * 2);
			payLoad.i_offset = 0;
			payLoad.setTransaction(a_trans);
		}
	}

	YapWriter getByteLoad() {
		return payLoad;
	}

	final YapWriter getPayLoad() {
		return payLoad;
	}
	
	final MsgD getWriterForLength(Transaction a_trans, int length) {
		MsgD message = (MsgD)clone(a_trans);
		message.payLoad = new YapWriter(a_trans, length + YapConst.MESSAGE_LENGTH);
		message.writeInt(i_msgID);
		message.writeInt(length);
		if(a_trans.i_parentTransaction == null){
		    message.payLoad.append(YapConst.SYSTEM_TRANS);
		}else{
		    message.payLoad.append(YapConst.USER_TRANS);
		}
		return message;
	}
	
	final MsgD getWriter(Transaction a_trans){
		return getWriterForLength(a_trans, 0);
	}

	final MsgD getWriterFor2Ints(Transaction a_trans, int id, int anInt) {
		MsgD message = getWriterForLength(a_trans, YapConst.YAPINT_LENGTH * 2);
		message.writeInt(id);
		message.writeInt(anInt);
		return message;
	}

	final MsgD getWriterFor3Ints(Transaction a_trans, int int1, int int2, int int3) {
		MsgD message = getWriterForLength(a_trans, YapConst.YAPINT_LENGTH * 3);
		message.writeInt(int1);
		message.writeInt(int2);
		message.writeInt(int3);
		return message;
	}
	
	final MsgD getWriterFor4Ints(Transaction a_trans, int int1, int int2, int int3, int int4) {
		MsgD message = getWriterForLength(a_trans, YapConst.YAPINT_LENGTH * 4);
		message.writeInt(int1);
		message.writeInt(int2);
		message.writeInt(int3);
		message.writeInt(int4);
		return message;
	}
	
	final MsgD getWriterForIntArray(Transaction a_trans, int[] ints, int length){
		MsgD message = getWriterForLength(a_trans, YapConst.YAPINT_LENGTH * (length + 1));
		message.writeInt(length);
		for (int i = 0; i < length; i++) {
			message.writeInt(ints[i]);
		}
		return message;
	}

	final MsgD getWriterForInt(Transaction a_trans, int id) {
		MsgD message = getWriterForLength(a_trans, YapConst.YAPINT_LENGTH);
		message.writeInt(id);
		return message;
	}
	
	final MsgD getWriterForIntString(Transaction a_trans,int anInt, String str) {
		MsgD message = getWriterForLength(a_trans, YapConst.stringIO.length(str) + YapConst.YAPINT_LENGTH * 2);
		message.writeInt(anInt);
		message.writeString(str);
		return message;
	}
	
	final MsgD getWriterForLong(Transaction a_trans, long a_long){
		MsgD message = getWriterForLength(a_trans, YapConst.YAPLONG_LENGTH);
		message.writeLong(a_long);
		return message;
	}
	

	final MsgD getWriterForString(Transaction a_trans, String str) {
		MsgD message = getWriterForLength(a_trans, YapConst.stringIO.length(str) + YapConst.YAPINT_LENGTH);
		message.writeString(str);
		return message;
	}

	MsgD getWriter(YapWriter bytes) {
		MsgD message = getWriterForLength(bytes.getTransaction(), bytes.getLength());
		message.payLoad.append(bytes.i_bytes);
		return message;
	}
	
	byte[] readBytes(){
	    return payLoad.readBytes(readInt());
	}

	final int readInt() {
		return payLoad.readInt();
	}
	
	final long readLong(){
	    return YLong.readLong(payLoad);
	}

	final Msg readPayLoad(Transaction a_trans, YapSocket sock, YapWriter reader)
		throws IOException {
		int length = reader.readInt();
		if((reader.readByte() == YapConst.SYSTEM_TRANS)  && (a_trans.i_parentTransaction != null)){
		    a_trans = a_trans.i_parentTransaction;
		}
		final MsgD command = (MsgD)clone(a_trans);
		command.payLoad = new YapWriter(a_trans, length);
		command.payLoad.read(sock);
		return command;
	}

	final String readString() {
		int length = readInt();
		return YapConst.stringIO.read(payLoad, length);
	}
	
	final void writeBytes(byte[] aBytes){
	    writeInt(aBytes.length);
	    payLoad.append(aBytes);
	}

	final void writeInt(int aInt) {
		payLoad.writeInt(aInt);
	}
	
	final void writeLong(long aLong){
	    YLong.writeLong(aLong, payLoad);
	}

	final void writeString(String aStr) {
		payLoad.writeInt(aStr.length());
		YapConst.stringIO.write(payLoad, aStr);
	}

}
