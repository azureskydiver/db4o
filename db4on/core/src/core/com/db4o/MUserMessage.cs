namespace com.db4o
{
	internal sealed class MUserMessage : com.db4o.MsgObject
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			if (stream.i_config.MessageRecipient() != null)
			{
				this.Unmarshall();
				stream.i_config.MessageRecipient().ProcessMessage(stream, stream.Unmarshall(_payLoad
					));
			}
			return true;
		}
	}
}
