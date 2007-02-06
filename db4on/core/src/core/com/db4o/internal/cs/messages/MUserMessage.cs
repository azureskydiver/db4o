namespace com.db4o.@internal.cs.messages
{
	public sealed class MUserMessage : com.db4o.@internal.cs.messages.MsgObject
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			if (MessageRecipient() != null)
			{
				Unmarshall();
				MessageRecipient().ProcessMessage(Stream(), Stream().Unmarshall(_payLoad));
			}
			return true;
		}

		private com.db4o.messaging.MessageRecipient MessageRecipient()
		{
			return Config().MessageRecipient();
		}
	}
}
