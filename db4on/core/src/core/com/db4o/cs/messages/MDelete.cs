namespace com.db4o.cs.messages
{
	public sealed class MDelete : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapReader bytes = this.GetByteLoad();
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
					catch
					{
					}
				}
			}
			return true;
		}
	}
}
