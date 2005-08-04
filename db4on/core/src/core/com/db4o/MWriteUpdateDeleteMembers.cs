namespace com.db4o
{
	internal sealed class MWriteUpdateDeleteMembers : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				this.getTransaction().writeUpdateDeleteMembers(readInt(), stream.getYapClass(readInt
					()), readInt(), readInt());
			}
			return true;
		}
	}
}
