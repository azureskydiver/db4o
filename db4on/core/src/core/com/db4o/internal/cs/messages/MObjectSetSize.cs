namespace com.db4o.@internal.cs.messages
{
	public class MObjectSetSize : com.db4o.@internal.cs.messages.MObjectSet
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = QueryResult(serverThread
				, ReadInt());
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECTSET_SIZE.GetWriterForInt
				(Transaction(), queryResult.Size()));
			return true;
		}
	}
}
