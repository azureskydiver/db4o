namespace com.db4o.@internal.cs.messages
{
	public sealed class MGetAll : com.db4o.@internal.cs.messages.MsgQuery
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			com.db4o.config.QueryEvaluationMode evaluationMode = com.db4o.config.QueryEvaluationMode
				.FromInt(ReadInt());
			WriteQueryResult(GetAll(evaluationMode), serverThread, evaluationMode);
			return true;
		}

		private com.db4o.@internal.query.result.AbstractQueryResult GetAll(com.db4o.config.QueryEvaluationMode
			 mode)
		{
			lock (StreamLock())
			{
				try
				{
					return File().GetAll(Transaction(), mode);
				}
				catch (System.Exception e)
				{
				}
				return NewQueryResult(mode);
			}
		}
	}
}
