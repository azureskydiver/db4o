namespace com.db4o
{
	internal sealed class MGetClasses : com.db4o.MsgD
	{
		internal sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					stream.i_classCollection.Write(GetTransaction());
				}
				catch (System.Exception e)
				{
				}
			}
			com.db4o.MsgD message = com.db4o.Msg.GET_CLASSES.GetWriterForLength(GetTransaction
				(), com.db4o.YapConst.INT_LENGTH + 1);
			com.db4o.YapWriter writer = message.GetPayLoad();
			writer.WriteInt(stream.i_classCollection.GetID());
			writer.Append(stream.StringIO().EncodingByte());
			message.Write(stream, sock);
			return true;
		}
	}
}
