/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.messaging;

import com.db4o.messaging.internal.*;


/**
 * Additional message-related information.
 */
public abstract class MessageContext {
	
	/**
	 * The context associated to the current message.
	 * 
	 * Only valid during {@link MessageRecipient#processMessage(com.db4o.ObjectContainer, Object)}
	 * 
	 * @sharpen.property
	 */
	public static MessageContext current() {
		return MessageContextInfrastructure.context();
	}
	
	/**
	 * The sender of the current message.
	 * 
	 * The reference can be used to send a reply to it.
	 * 
	 * @return
	 * 
	 * @sharpen.property
	 */
	public abstract MessageSender sender();
}
