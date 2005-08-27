
namespace com.db4o
{
	internal class MTaBeginEndSet : com.db4o.Msg
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			lock (getStream().i_lock)
			{
				getTransaction().beginEndSet();
				return true;
			}
		}
	}
}
