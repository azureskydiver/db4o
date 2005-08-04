namespace com.db4o
{
	internal sealed class MTaIsDeleted : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				bool isDeleted = getTransaction().isDeleted(this.readInt());
				int ret = isDeleted ? 1 : 0;
				com.db4o.Msg.TA_IS_DELETED.getWriterForInt(getTransaction(), ret).write(stream, sock
					);
			}
			return true;
		}
	}
}
