namespace com.db4o.@internal.cs.messages
{
	public class MProcessDeletes : com.db4o.@internal.cs.messages.Msg
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			lock (StreamLock())
			{
				Transaction().ProcessDeletes();
				return true;
			}
		}
	}
}
