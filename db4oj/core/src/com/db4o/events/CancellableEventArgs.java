package com.db4o.events;

public interface CancellableEventArgs extends EventArgs {
	
	public boolean isCancelled();
	
	public void cancel();

}
