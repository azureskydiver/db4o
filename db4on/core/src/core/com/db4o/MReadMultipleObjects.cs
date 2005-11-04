namespace com.db4o
{
	internal sealed class MReadMultipleObjects : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int size = readInt();
			com.db4o.MsgD[] ret = new com.db4o.MsgD[size];
			int length = (1 + size) * com.db4o.YapConst.YAPINT_LENGTH;
			com.db4o.YapStream stream = getStream();
			com.db4o.YapWriter bytes = null;
			lock (stream.i_lock)
			{
				for (int i = 0; i < size; i++)
				{
					int id = this.payLoad.readInt();
					try
					{
						bytes = stream.readWriterByID(getTransaction(), id);
					}
					catch (System.Exception e)
					{
						bytes = null;
					}
					if (bytes != null)
					{
						try
						{
							com.db4o.YapClassAny.appendEmbedded(bytes);
						}
						catch (System.Exception e)
						{
						}
						ret[i] = com.db4o.Msg.OBJECT_TO_CLIENT.getWriter(bytes);
						length += ret[i].payLoad.getLength();
					}
				}
			}
			com.db4o.MsgD multibytes = com.db4o.Msg.READ_MULTIPLE_OBJECTS.getWriterForLength(
				getTransaction(), length);
			multibytes.writeInt(size);
			for (int i = 0; i < size; i++)
			{
				if (ret[i] == null)
				{
					multibytes.writeInt(0);
				}
				else
				{
					multibytes.writeInt(ret[i].payLoad.getLength());
					multibytes.payLoad.append(ret[i].payLoad._buffer);
				}
			}
			multibytes.write(stream, sock);
			return true;
		}
	}
}
