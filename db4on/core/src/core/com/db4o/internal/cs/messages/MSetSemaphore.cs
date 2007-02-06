namespace com.db4o.@internal.cs.messages
{
	public sealed class MSetSemaphore : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int timeout = ReadInt();
			string name = ReadString();
			com.db4o.@internal.LocalObjectContainer stream = (com.db4o.@internal.LocalObjectContainer
				)Stream();
			bool res = stream.SetSemaphore(Transaction(), name, timeout);
			if (res)
			{
				serverThread.Write(com.db4o.@internal.cs.messages.Msg.SUCCESS);
			}
			else
			{
				serverThread.Write(com.db4o.@internal.cs.messages.Msg.FAILED);
			}
			return true;
		}
	}
}
