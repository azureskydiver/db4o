/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.foundation.*;
import com.db4o.inside.query.*;

public abstract class MsgQuery extends MsgObject {
	
	private static final int ID_AND_SIZE = 2;
	
	private static int nextID;
	
	protected final void writeQueryResult(AbstractQueryResult queryResult, YapServerThread serverThread, boolean lazy) {
		
		int queryResultId = 0;
		int maxCount = 0;
		
		if(lazy){
			queryResultId = generateID();
			maxCount = config().prefetchObjectCount();  
		} else{
			maxCount = queryResult.size();
		}
		
		MsgD message = QUERY_RESULT.getWriterForLength(transaction(), bufferLength(maxCount));
		YapWriter writer = message.payLoad();
		writer.writeInt(queryResultId);
		
        IntIterator4 idIterator = queryResult.iterateIDs();
        
    	writer.writeIDs(idIterator, maxCount);
        
        if(queryResultId > 0){
			serverThread.mapQueryResultToID(new LazyClientObjectSetStub(queryResult, idIterator), queryResultId);
        }
        
		serverThread.write(message);
	}

	private int bufferLength(int maxCount) {
		return YapConst.INT_LENGTH * (maxCount + ID_AND_SIZE);
	}
	
	private static synchronized int generateID(){
		nextID ++;
		if(nextID < 0){
			nextID = 1;
		}
		return nextID;
	}
	
	protected AbstractQueryResult newQueryResult(boolean lazy){
		return stream().newQueryResult(transaction(), lazy);
	}

}
