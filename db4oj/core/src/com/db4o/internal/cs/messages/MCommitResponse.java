/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;


public class MCommitResponse extends MsgD {
	
	private static final int EXCEPTION_FLAG_BYTE = 1;
	
	private static final byte WITH_EXCEPTION = (byte)1;
	
	private static final byte NO_EXCEPTION = (byte)0;

	public static MsgD createWithException(Transaction trans, Db4oException db4oException){
		final SerializedGraph serialized = Serializer.marshall(trans.stream(), db4oException);
		int length = EXCEPTION_FLAG_BYTE + serialized.marshalledLength();
		MsgD msg = Msg.COMMIT_RESPONSE.getWriterForLength(trans, length);
		msg._payLoad.append(WITH_EXCEPTION);
		serialized.write(msg._payLoad);
		return msg;
	}
	
	public static MsgD createWithoutException(Transaction trans){
		return Msg.COMMIT_RESPONSE.getWriterForByte(trans, NO_EXCEPTION);
	}
	
	public Db4oException readException(){
		byte b = _payLoad.readByte();
		if(b == NO_EXCEPTION){
			return null;
		}
		return (Db4oException)Serializer.unmarshall(stream(), SerializedGraph.read(_payLoad));
	}

}
