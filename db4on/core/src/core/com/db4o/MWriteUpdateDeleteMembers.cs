namespace com.db4o
{
	internal sealed class MWriteUpdateDeleteMembers : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				this.GetTransaction().WriteUpdateDeleteMembers(ReadInt(), stream.GetYapClass(ReadInt
					()), ReadInt(), ReadInt());
			}
			return true;
		}
	}
}
