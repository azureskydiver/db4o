namespace com.db4o
{
	internal sealed class MGetAll : com.db4o.Msg
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.QResult qr;
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				try
				{
					qr = stream.get1(getTransaction(), null)._delegate;
				}
				catch (System.Exception e)
				{
					qr = new com.db4o.QResult(getTransaction());
				}
			}
			this.writeQueryResult(getTransaction(), qr, sock);
			return true;
		}
	}
}
