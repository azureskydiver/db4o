namespace com.db4o
{
	internal sealed class MQueryExecute : com.db4o.MsgObject
	{
		internal override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.Transaction trans = getTransaction();
			com.db4o.YapStream stream = getStream();
			com.db4o.QueryResultImpl qr = new com.db4o.QueryResultImpl(trans);
			this.unmarshall();
			lock (stream.i_lock)
			{
				com.db4o.QQuery query = (com.db4o.QQuery)stream.unmarshall(_payLoad);
				query.unmarshall(getTransaction());
				try
				{
					query.executeLocal(qr);
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QueryResultImpl(getTransaction());
				}
			}
			writeQueryResult(getTransaction(), qr, sock);
			return true;
		}
	}
}
