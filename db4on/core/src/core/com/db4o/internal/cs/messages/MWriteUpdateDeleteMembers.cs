namespace com.db4o.@internal.cs.messages
{
	public sealed class MWriteUpdateDeleteMembers : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			lock (StreamLock())
			{
				Transaction().WriteUpdateDeleteMembers(ReadInt(), Stream().GetYapClass(ReadInt())
					, ReadInt(), ReadInt());
			}
			return true;
		}
	}
}
