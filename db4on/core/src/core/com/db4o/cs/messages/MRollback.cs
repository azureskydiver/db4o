namespace com.db4o.cs.messages
{
	public sealed class MRollback : com.db4o.cs.messages.Msg
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			this.GetTransaction().Rollback();
			return true;
		}
	}
}
