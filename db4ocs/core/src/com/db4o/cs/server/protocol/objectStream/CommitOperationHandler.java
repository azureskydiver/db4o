package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:40:39 AM
 */
public class CommitOperationHandler implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		//System.out.println("committing");
		session.getObjectContainer(context).commit();
	}
}
