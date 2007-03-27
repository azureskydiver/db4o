/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;



/**
 * @exclude
 */
public class MObjectSetReset extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		stub(readInt()).reset();
		return true;
	}

}
