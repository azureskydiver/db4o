namespace com.db4o
{
	internal sealed class MReadObject : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = null;
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				try
				{
					bytes = stream.readWriterByID(this.getTransaction(), this._payLoad.readInt());
				}
				catch (System.Exception e)
				{
					bytes = null;
				}
			}
			if (bytes == null)
			{
				bytes = new com.db4o.YapWriter(this.getTransaction(), 0, 0);
			}
			com.db4o.Msg.OBJECT_TO_CLIENT.getWriter(bytes).write(stream, sock);
			return true;
		}
	}
}
