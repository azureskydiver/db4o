namespace com.db4o
{
	internal sealed class MCommitOK : com.db4o.Msg
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			getTransaction().commit();
			com.db4o.Msg.OK.write(getStream(), _in);
			return true;
		}
	}
}
