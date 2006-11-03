namespace com.db4o.cs.messages
{
	public class MTaBeginEndSet : com.db4o.cs.messages.Msg
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
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
