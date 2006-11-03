namespace com.db4o.cs.messages
{
	public sealed class MGetInternalIDs : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapReader bytes = this.GetByteLoad();
			long[] ids;
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					ids = stream.GetYapClass(bytes.ReadInt()).GetIDs(GetTransaction());
				}
				catch
				{
					ids = new long[0];
				}
			}
			int size = ids.Length;
			com.db4o.cs.messages.MsgD message = com.db4o.cs.messages.Msg.ID_LIST.GetWriterForLength
				(GetTransaction(), com.db4o.YapConst.ID_LENGTH * (size + 1));
			com.db4o.YapReader writer = message.PayLoad();
			writer.WriteInt(size);
			for (int i = 0; i < size; i++)
			{
				writer.WriteInt((int)ids[i]);
			}
			message.Write(stream, sock);
			return true;
		}
	}
}
