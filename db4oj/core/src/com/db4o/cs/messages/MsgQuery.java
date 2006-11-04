/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.foundation.network.*;
import com.db4o.inside.query.*;


public abstract class MsgQuery extends MsgObject {
	
	private static int nextID;
	
	private int _queryResultId;
	
	private AbstractQueryResult _queryResult; 
	
	final void writeQueryResult(AbstractQueryResult queryResult, YapServerThread serverThread) {
		
		_queryResult = queryResult;
		
		Transaction trans = getTransaction();
		YapStream stream = getStream();
		
		if(stream.config().lazyQueries()){
			_queryResultId = generateID();
		}
		
		int size = queryResult.size();
		MsgD message = QUERY_RESULT.getWriterForLength(trans, YapConst.ID_LENGTH * (size + 2));
		YapWriter writer = message.payLoad();
		writer.writeInt(_queryResultId);
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
	
	public int queryResultID(){
		return _queryResultId;
	}
	
	public AbstractQueryResult queryResult(){
		return _queryResult;
	}
	
	

}
