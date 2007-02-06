namespace com.db4o.@internal.cs.messages
{
	/// <exclude></exclude>
	public class MObjectSetReset : com.db4o.@internal.cs.messages.MObjectSet
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			Stub(serverThread, ReadInt()).Reset();
			return true;
		}
	}
}
