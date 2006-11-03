namespace com.db4o.cs.messages
{
	public sealed class MTaIsDeleted : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				bool isDeleted = GetTransaction().IsDeleted(this.ReadInt());
				int ret = isDeleted ? 1 : 0;
				com.db4o.cs.messages.Msg.TA_IS_DELETED.GetWriterForInt(GetTransaction(), ret).Write
					(stream, sock);
			}
			return true;
		}
	}
}
