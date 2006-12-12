package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.common.util.Db4oUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:34:30 PM
 */
public class CloseOperationHandler implements OperationHandler {
	public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		//System.out.println("closing connection");
		System.out.println("TOTAL OBJECTS SAVED: " + Db4oUtil.countAll(session.getObjectContainer()));
		boolean closed = session.close();

		if (com.db4o.cs.server.protocol.protocol1.SetOperationHandler.stopWatchInsantiation.count() > 0) {
			System.out.println("setOperation count: " + com.db4o.cs.server.protocol.protocol1.SetOperationHandler.stopWatchInsantiation.count() + " duration: " + com.db4o.cs.server.protocol.protocol1.SetOperationHandler.stopWatchInsantiation.totalDuration() + " average: " + com.db4o.cs.server.protocol.protocol1.SetOperationHandler.stopWatchInsantiation.average());
			com.db4o.cs.server.protocol.protocol1.SetOperationHandler.stopWatchInsantiation.reset();
		}
		if (DeleteOperationHandler.stopWatchInsantiation.count() > 0) {
			System.out.println("deleteOperation count: " + DeleteOperationHandler.stopWatchInsantiation.count() + " duration: " + DeleteOperationHandler.stopWatchInsantiation.totalDuration() + " average: " + DeleteOperationHandler.stopWatchInsantiation.average());
			DeleteOperationHandler.stopWatchInsantiation.reset();
		}
        if(QueryOperationHandler.stopWatchQuery.count() > 0){
            System.out.println("queryOperation count: " + QueryOperationHandler.stopWatchQuery.count() + " duration: " + QueryOperationHandler.stopWatchQuery.totalDuration() + " average: " + QueryOperationHandler.stopWatchQuery.average());
			QueryOperationHandler.stopWatchQuery.reset();
        }
		return closed;
	}
}
