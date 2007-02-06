namespace com.db4o.@internal.cs.messages
{
	public sealed class MTaIsDeleted : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			lock (StreamLock())
			{
				bool isDeleted = Transaction().IsDeleted(ReadInt());
				int ret = isDeleted ? 1 : 0;
				serverThread.Write(com.db4o.@internal.cs.messages.Msg.TA_IS_DELETED.GetWriterForInt
					(Transaction(), ret));
			}
			return true;
		}
	}
}
