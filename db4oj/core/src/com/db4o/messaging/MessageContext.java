/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.messaging;

import com.db4o.*;

/**
 * Additional message-related information.
 */
public interface MessageContext {
	
	/**
	 * The container the message was dispatched to.
	 * 
	 * @return
	 * 
	 * @sharpen.property
	 */
	ObjectContainer container();
	
	/**
	 * The sender of the current message.
	 * 
	 * The reference can be used to send a reply to it.
	 * 
	 * @return
	 * 
	 * @sharpen.property
	 */
	MessageSender sender();
}
