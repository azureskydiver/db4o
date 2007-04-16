/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;


/**
 * @exclude
 */
public interface ServerMessageDispatcher extends MessageDispatcher {

	public String name();

	public void queryResultFinalized(int queryResultID);

	public Socket4 socket();

	public int dispatcherID();

	public LazyClientObjectSetStub queryResultForID(int queryResultID);

	public void switchToMainFile();

	public void switchToFile(MSwitchToFile file);

	public void useTransaction(MUseTransaction transaction);

	public void mapQueryResultToID(LazyClientObjectSetStub stub, int queryResultId);

	public ObjectServerImpl server();

	public void login();

	public boolean close();
	
	public boolean caresAboutCommitted();
	
	public void caresAboutCommitted(boolean care);
	
	public void writeIfAlive(Msg msg);

	public CallbackObjectInfoCollections committedInfo();
	
	public void  committedInfo(CallbackObjectInfoCollections committedInfo);
	
}
