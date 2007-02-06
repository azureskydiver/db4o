namespace com.db4o.@internal.cs.messages
{
	internal sealed class MCommit : com.db4o.@internal.cs.messages.Msg
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			Transaction().Commit();
			return true;
		}
	}
}
