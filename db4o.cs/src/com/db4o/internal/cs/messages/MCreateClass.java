/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public final class MCreateClass extends MsgD implements ServerSideMessage {

	public final boolean processAtServer() {
		ObjectContainerBase stream = stream();
		Transaction trans = stream.systemTransaction();
		boolean ok = false;
		try {
			synchronized (streamLock()) {
			    ReflectClass claxx = trans.reflector().forName(readString());
	            if (claxx != null) {
					ClassMetadata classMetadata = stream.produceClassMetadata(claxx);
					if (classMetadata != null) {
						stream.checkStillToSet();
						StatefulBuffer returnBytes = stream.readWriterByID(trans, classMetadata.getID());
						MsgD createdClass = Msg.OBJECT_TO_CLIENT.getWriter(returnBytes);
						write(createdClass);
						ok = true;
					}
	            }
			}
		} catch (Db4oException e) {
			// TODO: send the exception to the client
		} finally {
			if (!ok) {
				write(Msg.FAILED);
			}
		}
		return true;
	}
}
