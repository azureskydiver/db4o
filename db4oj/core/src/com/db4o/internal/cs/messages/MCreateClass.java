/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.Db4oRuntimeException;
import com.db4o.internal.ClassMetadata;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.internal.StatefulBuffer;
import com.db4o.internal.Transaction;
import com.db4o.internal.cs.ServerMessageDispatcher;
import com.db4o.reflect.ReflectClass;

public final class MCreateClass extends MsgD {

	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		ObjectContainerBase stream = stream();
		Transaction trans = stream.getSystemTransaction();

		ReflectClass claxx = trans.reflector().forName(readString());
		if (claxx == null) {
			return writeFailedMessage(serverThread);
		}
		synchronized (streamLock()) {
			try {
				ClassMetadata yapClass = stream.produceYapClass(claxx);
				if (yapClass == null) {
					return writeFailedMessage(serverThread);
				}
				stream.checkStillToSet();
				yapClass.setStateDirty();
				yapClass.write(trans);
				trans.commit();
				StatefulBuffer returnBytes = stream.readWriterByID(trans,
						yapClass.getID());
				MsgD createdClass = Msg.OBJECT_TO_CLIENT.getWriter(returnBytes);
				serverThread.write(createdClass);
			} catch (Db4oRuntimeException e) {
				writeFailedMessage(serverThread);
			}
		}
		return true;
	}
}
