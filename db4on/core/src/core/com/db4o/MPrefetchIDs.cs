namespace com.db4o
{
	internal sealed class MPrefetchIDs : com.db4o.Msg
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapFile stream = (com.db4o.YapFile)getStream();
			com.db4o.MsgD reply = com.db4o.Msg.ID_LIST.getWriterForLength(getTransaction(), com.db4o.YapConst
				.YAPINT_LENGTH * com.db4o.YapConst.PREFETCH_ID_COUNT);
			lock (stream.i_lock)
			{
				for (int i = 0; i < com.db4o.YapConst.PREFETCH_ID_COUNT; i++)
				{
					reply.writeInt(stream.prefetchID());
				}
			}
			reply.write(stream, sock);
			return true;
		}
	}
}
