namespace com.db4o.@internal.cs.messages
{
	public abstract class MsgQuery : com.db4o.@internal.cs.messages.MsgObject
	{
		private const int ID_AND_SIZE = 2;

		private static int nextID;

		protected void WriteQueryResult(com.db4o.@internal.query.result.AbstractQueryResult
			 queryResult, com.db4o.@internal.cs.ServerMessageDispatcher serverThread, com.db4o.config.QueryEvaluationMode
			 evaluationMode)
		{
			int queryResultId = 0;
			int maxCount = 0;
			if (evaluationMode == com.db4o.config.QueryEvaluationMode.IMMEDIATE)
			{
				maxCount = queryResult.Size();
			}
			else
			{
				queryResultId = GenerateID();
				maxCount = Config().PrefetchObjectCount();
			}
			com.db4o.@internal.cs.messages.MsgD message = QUERY_RESULT.GetWriterForLength(Transaction
				(), BufferLength(maxCount));
			com.db4o.@internal.StatefulBuffer writer = message.PayLoad();
			writer.WriteInt(queryResultId);
			com.db4o.foundation.IntIterator4 idIterator = queryResult.IterateIDs();
			writer.WriteIDs(idIterator, maxCount);
			if (queryResultId > 0)
			{
				serverThread.MapQueryResultToID(new com.db4o.@internal.cs.LazyClientObjectSetStub
					(queryResult, idIterator), queryResultId);
			}
			serverThread.Write(message);
		}

		private int BufferLength(int maxCount)
		{
			return com.db4o.@internal.Const4.INT_LENGTH * (maxCount + ID_AND_SIZE);
		}

		private static int GenerateID()
		{
			lock (typeof(MsgQuery))
			{
				nextID++;
				if (nextID < 0)
				{
					nextID = 1;
				}
				return nextID;
			}
		}

		protected virtual com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult
			(com.db4o.config.QueryEvaluationMode mode)
		{
			return Stream().NewQueryResult(Transaction(), mode);
		}
	}
}
