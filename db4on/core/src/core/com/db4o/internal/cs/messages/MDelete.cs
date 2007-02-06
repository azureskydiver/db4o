namespace com.db4o.@internal.cs.messages
{
	public sealed class MDelete : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.Buffer bytes = this.GetByteLoad();
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			lock (StreamLock())
			{
				object obj = stream.GetByID1(Transaction(), bytes.ReadInt());
				bool userCall = bytes.ReadInt() == 1;
				if (obj != null)
				{
					try
					{
						stream.Delete1(Transaction(), obj, userCall);
					}
					catch
					{
					}
				}
			}
			return true;
		}
	}
}
