/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o.messaging
{
	/// <summary>message recipient for client/server messaging.</summary>
	/// <remarks>
	/// message recipient for client/server messaging.
	/// <br /><br />db4o allows using the client/server TCP connection to send
	/// messages from the client to the server. Any object that can be
	/// stored to a db4o database file may be used as a message.<br /><br />
	/// See the sample in ../com/db4o/samples/messaging/ on how to
	/// use the messaging feature. It is also used to stop the server
	/// in ../com/db4o/samples/clientserver/StopServer.java<br /><br />
	/// <b>See Also:</b><br />
	/// <see cref="com.db4o.config.Configuration.setMessageRecipient">Configuration.setMessageRecipient(MessageRecipient)
	/// 	</see>
	/// , <br />
	/// <see cref="com.db4o.messaging.MessageSender">com.db4o.messaging.MessageSender</see>
	/// ,<br />
	/// <see cref="com.db4o.config.Configuration.getMessageSender">com.db4o.config.Configuration.getMessageSender
	/// 	</see>
	/// ,<br />
	/// </remarks>
	public interface MessageRecipient
	{
		/// <summary>the method called upon the arrival of messages.</summary>
		/// <remarks>the method called upon the arrival of messages.</remarks>
		/// <param name="con">the ObjectContainer the message was sent to.</param>
		/// <param name="message">the message received.</param>
		void processMessage(com.db4o.ObjectContainer con, object message);
	}
}
