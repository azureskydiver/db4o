namespace com.db4o.@internal.cs.messages
{
	public sealed class MRollback : com.db4o.@internal.cs.messages.Msg
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			Transaction().Rollback();
			return true;
		}
	}
}
