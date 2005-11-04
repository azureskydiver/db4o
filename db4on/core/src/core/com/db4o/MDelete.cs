namespace com.db4o
{
	internal sealed class MDelete : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = this.getByteLoad();
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				object obj = stream.getByID1(getTransaction(), bytes.readInt());
				bool userCall = bytes.readInt() == 1;
				if (obj != null)
				{
					try
					{
						stream.delete1(getTransaction(), obj, userCall);
					}
					catch (System.Exception e)
					{
					}
				}
			}
			return true;
		}
	}
}
