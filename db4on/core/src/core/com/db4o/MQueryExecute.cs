namespace com.db4o
{
	internal sealed class MQueryExecute : com.db4o.MsgObject
	{
		internal override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.Transaction trans = GetTransaction();
			com.db4o.YapStream stream = GetStream();
			com.db4o.QueryResultImpl qr = new com.db4o.QueryResultImpl(trans);
			this.Unmarshall();
			lock (stream.i_lock)
			{
				com.db4o.QQuery query = (com.db4o.QQuery)stream.Unmarshall(_payLoad);
				query.Unmarshall(GetTransaction());
				try
				{
					query.ExecuteLocal(qr);
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QueryResultImpl(GetTransaction());
				}
			}
			WriteQueryResult(GetTransaction(), qr, sock);
			return true;
		}
	}
}
