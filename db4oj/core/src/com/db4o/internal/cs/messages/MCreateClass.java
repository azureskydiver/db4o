/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

public final class MCreateClass extends MsgD implements ServerSideMessage {

	public final boolean processAtServer() {
		ObjectContainerBase stream = stream();
		Transaction trans = stream.getSystemTransaction();
		ReflectClass claxx = trans.reflector().forName(readString());
		boolean ok = false;
		try {
			if (claxx != null) {
				synchronized (streamLock()) {
					ClassMetadata yapClass = stream.produceClassMetadata(claxx);
					if (yapClass != null) {
						stream.checkStillToSet();
						yapClass.setStateDirty();
						yapClass.write(trans);
						trans.commit();
						StatefulBuffer returnBytes = stream.readWriterByID(trans, yapClass.getID());
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
