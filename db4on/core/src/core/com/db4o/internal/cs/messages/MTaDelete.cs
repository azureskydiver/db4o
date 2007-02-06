namespace com.db4o.@internal.cs.messages
{
	public class MTaDelete : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int id = _payLoad.ReadInt();
			int cascade = _payLoad.ReadInt();
			com.db4o.@internal.Transaction trans = Transaction();
			lock (StreamLock())
			{
				trans.Delete(null, id, cascade);
				return true;
			}
		}
	}
}
