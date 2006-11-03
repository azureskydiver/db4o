namespace com.db4o.cs.messages
{
	public sealed class MGetClasses : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					stream.ClassCollection().Write(GetTransaction());
				}
				catch
				{
				}
			}
			com.db4o.cs.messages.MsgD message = com.db4o.cs.messages.Msg.GET_CLASSES.GetWriterForLength
				(GetTransaction(), com.db4o.YapConst.INT_LENGTH + 1);
			com.db4o.YapReader writer = message.PayLoad();
			writer.WriteInt(stream.ClassCollection().GetID());
			writer.Append(stream.StringIO().EncodingByte());
			message.Write(stream, sock);
			return true;
		}
	}
}
