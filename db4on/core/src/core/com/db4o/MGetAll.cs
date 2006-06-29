namespace com.db4o
{
	internal sealed class MGetAll : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.QueryResultImpl qr;
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					qr = (com.db4o.QueryResultImpl)stream.Get1(GetTransaction(), null)._delegate;
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QueryResultImpl(GetTransaction());
				}
			}
			this.WriteQueryResult(GetTransaction(), qr, sock);
			return true;
		}
	}
}
