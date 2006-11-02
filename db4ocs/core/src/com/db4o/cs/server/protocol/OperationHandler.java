package com.db4o.cs.server.protocol;

import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:13:08 AM
 */
public interface OperationHandler {
	void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException;
}
