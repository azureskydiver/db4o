namespace com.db4o
{
	internal sealed class MTaIsDeleted : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				bool isDeleted = GetTransaction().IsDeleted(this.ReadInt());
				int ret = isDeleted ? 1 : 0;
				com.db4o.Msg.TA_IS_DELETED.GetWriterForInt(GetTransaction(), ret).Write(stream, sock
					);
			}
			return true;
		}
	}
}
