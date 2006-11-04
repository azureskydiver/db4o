/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.foundation.*;
import com.db4o.inside.query.*;

public abstract class MsgQuery extends MsgObject {
	
	private static final int ID_AND_SIZE = 2;
	
	private static int nextID;
	
	protected final void writeQueryResult(AbstractQueryResult queryResult, YapServerThread serverThread) {
		
		int queryResultId = 0;
		int transferSize = 0;
		
		if(config().lazyQueries()){
			queryResultId = generateID();
			serverThread.mapQueryResultToID(queryResult, queryResultId);
			transferSize = config().prefetchObjectCount();  
		} else{
			transferSize = queryResult.size();
		}
		
		int bufferLength = YapConst.INT_LENGTH * (transferSize + ID_AND_SIZE);
		
		MsgD message = QUERY_RESULT.getWriterForLength(transaction(), bufferLength);
		YapWriter writer = message.payLoad();
		writer.writeInt(queryResultId);
		
    	int savedOffset = writer._offset; 
        writer.writeInt(0);
        int actualsize = 0;
        IntIterator4 idIterator = queryResult.iterateIDs();
        while(idIterator.moveNext()){
            writer.writeInt(idIterator.currentInt());
            actualsize ++;
            if(actualsize >= transferSize){
            	break;
            }
        }
        int secondSavedOffset = writer._offset;
        writer._offset = savedOffset;
        writer.writeInt(actualsize);
        writer._offset = secondSavedOffset;
		serverThread.write(message);
	}
	
	private static synchronized int generateID(){
		nextID ++;
		if(nextID < 0){
			nextID = 1;
		}
		return nextID;
	}
	
	protected AbstractQueryResult newQueryResult(){
		return stream().newQueryResult(transaction());
	}

}
