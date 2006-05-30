namespace com.db4o
{
	internal sealed class MGetInternalIDs : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = this.GetByteLoad();
			long[] ids;
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					ids = stream.GetYapClass(bytes.ReadInt()).GetIDs(GetTransaction());
				}
				catch (System.Exception e)
				{
					ids = new long[0];
				}
			}
			int size = ids.Length;
			com.db4o.MsgD message = com.db4o.Msg.ID_LIST.GetWriterForLength(GetTransaction(), 
				com.db4o.YapConst.YAPID_LENGTH * (size + 1));
			com.db4o.YapWriter writer = message.GetPayLoad();
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
