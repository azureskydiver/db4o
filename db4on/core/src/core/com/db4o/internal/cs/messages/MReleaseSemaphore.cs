namespace com.db4o.@internal.cs.messages
{
	public sealed class MReleaseSemaphore : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			string name = ReadString();
			((com.db4o.@internal.LocalObjectContainer)Stream()).ReleaseSemaphore(Transaction(
				), name);
			return true;
		}
	}
}
