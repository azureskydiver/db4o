/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


class ShutDownRunnable extends Collection4 implements Runnable {
	// FIXME: aggregate Collection4 instead of extending it
	
	public volatile boolean dontRemove = false;
	
	public void run(){
		dontRemove = true;
		Collection4 copy=new Collection4(this);
		Iterator4 i = copy.iterator();
		while(i.moveNext()){
			((ObjectContainerBase)i.current()).failedToShutDown();
		}
	}
	
}

