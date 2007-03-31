/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;

public class MDb4oException extends MsgD {

	public MDb4oException clone(Transaction trans, Db4oException e) {
		SerializedGraph serialized = Serializer.marshall(trans.stream(), e);
		MDb4oException msg = (MDb4oException) getWriterForLength(trans, serialized
				.marshalledLength());
		serialized.write(msg._payLoad);
		return msg;
	}

	public Db4oException exception() {
		return (Db4oException) Serializer.unmarshall(stream(), SerializedGraph
				.read(_payLoad));
	}
}
