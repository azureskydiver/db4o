
namespace com.db4o
{
	internal sealed class MGetInternalIDs : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter bytes = this.getByteLoad();
			long[] ids;
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				try
				{
					ids = stream.getYapClass(bytes.readInt()).getIDs(getTransaction());
				}
				catch (System.Exception e)
				{
					ids = new long[0];
				}
			}
			int size = ids.Length;
			com.db4o.MsgD message = com.db4o.Msg.ID_LIST.getWriterForLength(getTransaction(), 
				com.db4o.YapConst.YAPID_LENGTH * (size + 1));
			com.db4o.YapWriter writer = message.getPayLoad();
			writer.writeInt(size);
			for (int i = 0; i < size; i++)
			{
				writer.writeInt((int)ids[i]);
			}
			message.write(stream, sock);
			return true;
		}
	}
}
