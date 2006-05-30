namespace com.db4o
{
	internal sealed class MRollback : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			this.GetTransaction().Rollback();
			return true;
		}
	}
}
