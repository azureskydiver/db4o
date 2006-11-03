namespace com.db4o.cs.messages
{
	public sealed class MUserMessage : com.db4o.cs.messages.MsgObject
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			if (stream.ConfigImpl().MessageRecipient() != null)
			{
				this.Unmarshall();
				stream.ConfigImpl().MessageRecipient().ProcessMessage(stream, stream.Unmarshall(_payLoad
					));
			}
			return true;
		}
	}
}
