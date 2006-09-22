namespace com.db4o
{
	internal sealed class MPrefetchIDs : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			int prefetchIDCount = ReadInt();
			com.db4o.MsgD reply = com.db4o.Msg.ID_LIST.GetWriterForLength(GetTransaction(), com.db4o.YapConst
				.INT_LENGTH * prefetchIDCount);
			lock (stream.i_lock)
			{
				for (int i = 0; i < prefetchIDCount; i++)
				{
					reply.WriteInt(stream.PrefetchID());
				}
			}
			reply.Write(stream, sock);
			return true;
		}
	}
}
