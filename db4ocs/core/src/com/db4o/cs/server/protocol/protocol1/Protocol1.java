package com.db4o.cs.server.protocol.protocol1;

import com.db4o.cs.server.protocol.Protocol;
import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.protocol.objectStream.ObjectStreamProtocol;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.common.Operations;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 12:19:58 PM
 */
public class Protocol1 extends ObjectStreamProtocol implements Protocol {

	public Protocol1(Context context, Session session) {
		super(context, session);
	}


	public OperationHandler getOperationHandler(byte operation) {

		// override some operations
		if(operation == Operations.CLASS_METADATA){
			return new ClassMetaDataOperationHandler();
		} else if (operation == Operations.SET) {
			return new SetOperationHandler();
		}

		return super.getOperationHandler(operation);
	}
}
