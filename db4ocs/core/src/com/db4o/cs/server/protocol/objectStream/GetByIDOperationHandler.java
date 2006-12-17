/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.server.protocol.objectStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.ext.ExtObjectContainer;

public class GetByIDOperationHandler implements OperationHandler {

	public Object handle(Context context, Session session,
			ObjectInputStream oin, ObjectOutputStream oout) throws IOException,
			ClassNotFoundException {
		Long id = oin.readLong();
		ExtObjectContainer oc = session.getObjectContainer().ext();
		Object o =  oc.getByID(id);
		oc.activate(o, 1);
		oout.writeObject(o);
		return null;
	}

}
