namespace com.db4o.@internal.cs.messages
{
	public sealed class MReadObject : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.StatefulBuffer bytes = null;
			lock (StreamLock())
			{
				try
				{
					bytes = Stream().ReadWriterByID(Transaction(), _payLoad.ReadInt());
				}
				catch
				{
				}
			}
			if (bytes == null)
			{
				bytes = new com.db4o.@internal.StatefulBuffer(Transaction(), 0, 0);
			}
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.GetWriter(
				bytes));
			return true;
		}
	}
}
