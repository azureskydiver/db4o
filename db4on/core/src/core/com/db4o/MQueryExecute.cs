namespace com.db4o
{
	internal sealed class MQueryExecute : com.db4o.MsgObject
	{
		internal override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.Transaction trans = getTransaction();
			com.db4o.YapStream stream = getStream();
			com.db4o.QResult qr = new com.db4o.QResult(trans);
			this.unmarshall();
			com.db4o.QQuery query = (com.db4o.QQuery)stream.unmarshall(payLoad);
			query.unmarshall(getTransaction());
			lock (stream.i_lock)
			{
				try
				{
					query.executeLocal(qr);
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QResult(getTransaction());
				}
			}
			writeQueryResult(getTransaction(), qr, sock);
			return true;
		}
	}
}
