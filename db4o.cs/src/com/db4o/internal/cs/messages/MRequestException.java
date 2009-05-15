/* Copyright (C) 2009   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;

public class MRequestException extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
		throw new Db4oException();
	}

}
