namespace com.db4o.cs.messages
{
	public sealed class MQueryExecute : com.db4o.cs.messages.MsgObject
	{
		public override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			Unmarshall();
			WriteQueryResult(Execute(), sock);
			return true;
		}

		private com.db4o.inside.query.QueryResult Execute()
		{
			lock (StreamLock())
			{
				com.db4o.Transaction trans = GetTransaction();
				com.db4o.YapStream stream = GetStream();
				com.db4o.QQuery query = (com.db4o.QQuery)stream.Unmarshall(_payLoad);
				query.Unmarshall(trans);
				return ExecuteFully(trans, stream, query);
			}
		}

		private com.db4o.inside.query.QueryResult ExecuteFully(com.db4o.Transaction trans
			, com.db4o.YapStream stream, com.db4o.QQuery query)
		{
			try
			{
				com.db4o.inside.query.QueryResult qr = stream.NewQueryResult(trans);
				qr.LoadFromQuery(query);
				return qr;
			}
			catch
			{
				return stream.NewQueryResult(trans);
			}
		}
	}
}
