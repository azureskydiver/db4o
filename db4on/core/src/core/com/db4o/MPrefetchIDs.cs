namespace com.db4o
{
	internal sealed class MPrefetchIDs : com.db4o.Msg
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapFile stream = (com.db4o.YapFile)GetStream();
			com.db4o.MsgD reply = com.db4o.Msg.ID_LIST.GetWriterForLength(GetTransaction(), com.db4o.YapConst
				.INT_LENGTH * com.db4o.YapConst.PREFETCH_ID_COUNT);
			lock (stream.i_lock)
			{
				for (int i = 0; i < com.db4o.YapConst.PREFETCH_ID_COUNT; i++)
				{
					reply.WriteInt(stream.PrefetchID());
				}
			}
			reply.Write(stream, sock);
			return true;
		}
	}
}
