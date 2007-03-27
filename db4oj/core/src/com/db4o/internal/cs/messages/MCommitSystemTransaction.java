/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;


/**
 * @exclude
 */
public class MCommitSystemTransaction extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
		transaction().systemTransaction().commit();
		return true;
	}

}
