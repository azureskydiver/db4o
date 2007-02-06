namespace com.db4o.@internal.cs.messages
{
	public sealed class MPrefetchIDs : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int prefetchIDCount = ReadInt();
			com.db4o.@internal.cs.messages.MsgD reply = com.db4o.@internal.cs.messages.Msg.ID_LIST
				.GetWriterForLength(Transaction(), com.db4o.@internal.Const4.INT_LENGTH * prefetchIDCount
				);
			lock (StreamLock())
			{
				for (int i = 0; i < prefetchIDCount; i++)
				{
					reply.WriteInt(((com.db4o.@internal.LocalObjectContainer)Stream()).PrefetchID());
				}
			}
			serverThread.Write(reply);
			return true;
		}
	}
}
