namespace com.db4o.@internal.cs.messages
{
	/// <exclude></exclude>
	public class MObjectSetGetId : com.db4o.@internal.cs.messages.MObjectSet
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = QueryResult(serverThread
				, ReadInt());
			int id = queryResult.GetId(ReadInt());
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECTSET_GET_ID.GetWriterForInt
				(Transaction(), id));
			return true;
		}
	}
}
