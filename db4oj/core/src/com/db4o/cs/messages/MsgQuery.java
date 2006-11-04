/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.query.*;

public abstract class MsgQuery extends MsgObject {
	
	private static int nextID;
	
	protected final void writeQueryResult(AbstractQueryResult queryResult, YapServerThread serverThread) {
		
		int queryResultId = 0;
		
		if(getStream().config().lazyQueries()){
			queryResultId = generateID();
			serverThread.mapQueryResultToID(queryResult, queryResultId);
		}
		
		int size = queryResult.size();
		MsgD message = QUERY_RESULT.getWriterForLength(getTransaction(), YapConst.ID_LENGTH * (size + 2));
		YapWriter writer = message.payLoad();
		writer.writeInt(queryResultId);
		writer.writeQueryResult(queryResult);
		serverThread.write(message);
	}
	
	private static synchronized int generateID(){
		nextID ++;
		if(nextID < 0){
			nextID = 1;
		}
		return nextID;
	}

}
