namespace com.db4o.cs.messages
{
	public sealed class MGetAll : com.db4o.cs.messages.Msg
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			WriteQueryResult(GetAll(), sock);
			return true;
		}

		private com.db4o.inside.query.QueryResult GetAll()
		{
			lock (StreamLock())
			{
				try
				{
					return GetStream().GetAll(GetTransaction());
				}
				catch (System.Exception e)
				{
				}
				return GetStream().NewQueryResult(GetTransaction());
			}
		}
	}
}
