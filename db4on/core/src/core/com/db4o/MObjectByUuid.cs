
namespace com.db4o
{
	internal class MObjectByUuid : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			long uuid = readLong();
			byte[] signature = readBytes();
			int id = 0;
			com.db4o.YapStream stream = getStream();
			com.db4o.Transaction trans = getTransaction();
			lock (stream.i_lock)
			{
				try
				{
					object[] arr = trans.objectAndYapObjectBySignature(uuid, signature);
					if (arr[1] != null)
					{
						com.db4o.YapObject yo = (com.db4o.YapObject)arr[1];
						id = yo.getID();
					}
				}
				catch (System.Exception e)
				{
				}
			}
			com.db4o.Msg.OBJECT_BY_UUID.getWriterForInt(trans, id).write(stream, sock);
			return true;
		}
	}
}
