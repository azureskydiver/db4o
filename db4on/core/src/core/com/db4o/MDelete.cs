namespace com.db4o
{
	internal sealed class MDelete : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = this.GetByteLoad();
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				object obj = stream.GetByID1(GetTransaction(), bytes.ReadInt());
				bool userCall = bytes.ReadInt() == 1;
				if (obj != null)
				{
					try
					{
						stream.Delete1(GetTransaction(), obj, userCall);
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
