/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

class JDK_1_3 extends JDK_1_2{

	Thread addShutdownHook(Runnable a_runnable){
		Thread thread = new Thread(a_runnable);
		try {
			invoke(Runtime.getRuntime(), "addShutdownHook", new Class[]{Thread.class}, new Object[]{thread});
		} catch (Throwable e) {
			Exceptions4.shouldNeverHappen();
		}
		return thread;
	}
	
	void removeShutdownHook(Thread a_thread){
		try {
			invoke(Runtime.getRuntime(), "removeShutdownHook", new Class[]{Thread.class}, new Object[]{a_thread});
		} catch (Throwable e) {
			Exceptions4.shouldNeverHappen();
		}
	}
	
	public int ver(){
	    return 3;
	}
	
}
