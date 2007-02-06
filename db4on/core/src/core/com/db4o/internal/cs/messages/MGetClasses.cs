namespace com.db4o.@internal.cs.messages
{
	public sealed class MGetClasses : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			lock (StreamLock())
			{
				try
				{
					stream.ClassCollection().Write(Transaction());
				}
				catch
				{
				}
			}
			com.db4o.@internal.cs.messages.MsgD message = com.db4o.@internal.cs.messages.Msg.
				GET_CLASSES.GetWriterForLength(Transaction(), com.db4o.@internal.Const4.INT_LENGTH
				 + 1);
			com.db4o.@internal.Buffer writer = message.PayLoad();
			writer.WriteInt(stream.ClassCollection().GetID());
			writer.Append(stream.StringIO().EncodingByte());
			serverThread.Write(message);
			return true;
		}
	}
}
