namespace com.db4o.@internal.cs.messages
{
	/// <exclude></exclude>
	public abstract class MObjectSet : com.db4o.@internal.cs.messages.MsgD
	{
		protected virtual com.db4o.@internal.query.result.AbstractQueryResult QueryResult
			(com.db4o.@internal.cs.ServerMessageDispatcher serverThread, int queryResultID)
		{
			return Stub(serverThread, queryResultID).QueryResult();
		}

		protected virtual com.db4o.@internal.cs.LazyClientObjectSetStub Stub(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread, int queryResultID)
		{
			return serverThread.QueryResultForID(queryResultID);
		}
	}
}
