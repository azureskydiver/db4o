
namespace com.db4o
{
	internal sealed class MGetClasses : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapStream stream = getStream();
			lock (stream.i_lock)
			{
				try
				{
					stream.i_classCollection.write(stream, getTransaction());
				}
				catch (System.Exception e)
				{
				}
			}
			com.db4o.MsgD message = com.db4o.Msg.GET_CLASSES.getWriterForLength(getTransaction
				(), com.db4o.YapConst.YAPINT_LENGTH + 1);
			com.db4o.YapWriter writer = message.getPayLoad();
			writer.writeInt(stream.i_classCollection.getID());
			writer.append(stream.stringIO().encodingByte());
			message.write(stream, sock);
			return true;
		}
	}
}
