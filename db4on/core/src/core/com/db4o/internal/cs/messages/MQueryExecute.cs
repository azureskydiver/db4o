namespace com.db4o.@internal.cs.messages
{
	public sealed class MQueryExecute : com.db4o.@internal.cs.messages.MsgQuery
	{
		private com.db4o.config.QueryEvaluationMode _evaluationMode;

		public override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			Unmarshall(_payLoad._offset);
			WriteQueryResult(Execute(), serverThread, _evaluationMode);
			return true;
		}

		private com.db4o.@internal.query.result.AbstractQueryResult Execute()
		{
			lock (StreamLock())
			{
				com.db4o.@internal.query.processor.QQuery query = (com.db4o.@internal.query.processor.QQuery
					)Stream().Unmarshall(_payLoad);
				query.Unmarshall(Transaction());
				_evaluationMode = query.EvaluationMode();
				return ExecuteFully(query);
			}
		}

		private com.db4o.@internal.query.result.AbstractQueryResult ExecuteFully(com.db4o.@internal.query.processor.QQuery
			 query)
		{
			try
			{
				com.db4o.@internal.query.result.AbstractQueryResult qr = NewQueryResult(query.EvaluationMode
					());
				qr.LoadFromQuery(query);
				return qr;
			}
			catch
			{
				return NewQueryResult(query.EvaluationMode());
			}
		}
	}
}
