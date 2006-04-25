namespace com.db4o
{
	internal sealed class MUserMessage : com.db4o.MsgObject
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			if (stream.i_config.messageRecipient() != null)
			{
				this.unmarshall();
				stream.i_config.messageRecipient().processMessage(stream, stream.unmarshall(_payLoad
					));
			}
			return true;
		}
	}
}
