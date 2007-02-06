namespace com.db4o.@internal.cs.messages
{
	/// <exclude></exclude>
	public class MObjectSetIndexOf : com.db4o.@internal.cs.messages.MObjectSet
	{
		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = QueryResult(serverThread
				, ReadInt());
			int id = queryResult.IndexOf(ReadInt());
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.OBJECTSET_INDEXOF.GetWriterForInt
				(Transaction(), id));
			return true;
		}
	}
}
