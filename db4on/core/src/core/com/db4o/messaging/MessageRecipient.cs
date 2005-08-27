
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
