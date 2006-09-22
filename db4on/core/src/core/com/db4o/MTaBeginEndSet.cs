namespace com.db4o
{
	internal class MTaBeginEndSet : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			lock (GetStream().i_lock)
			{
				GetTransaction().BeginEndSet();
				return true;
			}
		}
	}
}
