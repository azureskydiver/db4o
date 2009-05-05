/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
package com.db4o.internal.cs;

public interface BroadcastFilter {
	public boolean accept(ServerMessageDispatcher dispatcher);
}
