/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

public interface Event4 {
	
	public void addListener(EventListener4 listener);
	public void removeListener(EventListener4 listener);
}
