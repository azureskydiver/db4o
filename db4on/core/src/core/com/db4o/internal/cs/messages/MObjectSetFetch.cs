namespace com.db4o.@internal.cs.messages
{
	/// <exclude></exclude>
	public class MObjectSetFetch : com.db4o.@internal.cs.messages.MObjectSet
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int queryResultID = ReadInt();
			int fetchSize = ReadInt();
			com.db4o.foundation.IntIterator4 idIterator = Stub(serverThread, queryResultID).IdIterator
				();
			com.db4o.@internal.cs.messages.MsgD message = ID_LIST.GetWriterForLength(Transaction
				(), BufferLength(fetchSize));
			com.db4o.@internal.StatefulBuffer writer = message.PayLoad();
			writer.WriteIDs(idIterator, fetchSize);
			serverThread.Write(message);
			return true;
		}

		private int BufferLength(int fetchSize)
		{
			return com.db4o.@internal.Const4.INT_LENGTH * (fetchSize + 1);
		}
	}
}
