package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.common.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:40:39 AM
 */
public class CommitOperationHandler implements OperationHandler {
	public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		Log.print("committing");
		System.out.println("commit");
		session.getObjectContainer().commit();
		return true;
	}
}
