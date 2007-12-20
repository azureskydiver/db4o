/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.messaging.internal;

import com.db4o.foundation.*;
import com.db4o.messaging.*;


public class MessageContextInfrastructure {
	
	public static final ContextVariable contextProvider = new ContextVariable(MessageContextProvider.class);
	
	public static MessageContext context() {
		MessageContextProvider provider = (MessageContextProvider)contextProvider.value();
		if (provider == null) {
			return null;
		}
		return provider.messageContext();
	}
}
