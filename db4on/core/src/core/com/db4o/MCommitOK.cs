namespace com.db4o
{
	internal sealed class MCommitOK : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			GetTransaction().Commit();
			com.db4o.Msg.OK.Write(GetStream(), @in);
			return true;
		}
	}
}
