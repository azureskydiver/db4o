namespace com.db4o.@internal.cs.messages
{
	public sealed class MCommitOK : com.db4o.@internal.cs.messages.Msg
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			Transaction().Commit();
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OK);
			return true;
		}
	}
}
