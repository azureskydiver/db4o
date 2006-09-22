namespace com.db4o
{
	internal sealed class MCommit : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			GetTransaction().Commit();
			return true;
		}
	}
}
