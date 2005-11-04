namespace com.db4o
{
	internal sealed class MCommit : com.db4o.Msg
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			getTransaction().commit();
			return true;
		}
	}
}
